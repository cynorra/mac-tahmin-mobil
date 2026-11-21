// Çalıştırma (Firestore emulator gerekir):
//   npm i -D @firebase/rules-unit-testing firebase mocha
//   firebase emulators:exec --only firestore "npx mocha firestore.rules.test.js"
const fs = require('fs');
const assert = require('assert');
const { initializeTestEnvironment, assertFails, assertSucceeds } = require('@firebase/rules-unit-testing');
const { doc, setDoc, updateDoc, getDoc } = require('firebase/firestore');

const PROJECT_ID = 'matchpick-rules-test';
let env;

before(async () => {
    env = await initializeTestEnvironment({
        projectId: PROJECT_ID,
        firestore: { rules: fs.readFileSync('firestore.rules', 'utf8') },
    });
});
after(async () => { await env.cleanup(); });
beforeEach(async () => { await env.clearFirestore(); });

const dbFor = (uid) => env.authenticatedContext(uid).firestore();

describe('MatchPick Firestore Security Rules', () => {
    it('Kullanıcı sıfır puanla kendi profilini oluşturabilir', async () => {
        await assertSucceeds(setDoc(doc(dbFor('u1'), 'users/u1'), { uid: 'u1', totalPoints: 0, currentStreak: 0 }));
    });

    it('Kullanıcı yüksek puanla profil oluşturamaz', async () => {
        await assertFails(setDoc(doc(dbFor('u1'), 'users/u1'), { uid: 'u1', totalPoints: 99999 }));
    });

    it('Kullanıcı kendi puanını / serisini güncelleyemez', async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await setDoc(doc(ctx.firestore(), 'users/u1'), { uid: 'u1', totalPoints: 0, currentStreak: 0, bestStreak: 0 });
        });
        const ref = doc(dbFor('u1'), 'users/u1');
        await assertFails(updateDoc(ref, { totalPoints: 99999 }));
        await assertFails(updateDoc(ref, { currentStreak: 50 }));
        await assertFails(updateDoc(ref, { bestStreak: 50 }));
        await assertSucceeds(updateDoc(ref, { displayName: 'Eren' }));
    });

    it('Başkası adına tahmin oluşturulamaz', async () => {
        await assertFails(setDoc(doc(dbFor('attacker'), 'predictions/m1_victim'), {
            fixtureId: 'm1', userId: 'victim', pick: '1', status: 'PENDING',
        }));
    });

    it('Tahmin id si fixtureId_uid olmak zorunda (maç başına tek tahmin)', async () => {
        const db = dbFor('u1');
        await assertFails(setDoc(doc(db, 'predictions/random_id_1'), {
            fixtureId: 'm1', userId: 'u1', pick: '1', status: 'PENDING',
        }));
        await assertSucceeds(setDoc(doc(db, 'predictions/m1_u1'), {
            fixtureId: 'm1', userId: 'u1', pick: '1', status: 'PENDING',
        }));
    });

    it('Geçersiz pick veya status reddedilir', async () => {
        const db = dbFor('u1');
        await assertFails(setDoc(doc(db, 'predictions/m1_u1'), { fixtureId: 'm1', userId: 'u1', pick: '9', status: 'PENDING' }));
        await assertFails(setDoc(doc(db, 'predictions/m1_u1'), { fixtureId: 'm1', userId: 'u1', pick: '1', status: 'WON' }));
    });

    it('Bekleyen tahminde pick değişebilir, status değişemez', async () => {
        const db = dbFor('u1');
        const ref = doc(db, 'predictions/m1_u1');
        await assertSucceeds(setDoc(ref, { fixtureId: 'm1', userId: 'u1', pick: '1', status: 'PENDING' }));
        await assertSucceeds(updateDoc(ref, { pick: 'X' }));
        await assertFails(updateDoc(ref, { status: 'WON' }));
    });

    it('Sonuçlanmış tahmin değiştirilemez', async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await setDoc(doc(ctx.firestore(), 'predictions/m1_u1'), { fixtureId: 'm1', userId: 'u1', pick: '1', status: 'WON' });
        });
        await assertFails(updateDoc(doc(dbFor('u1'), 'predictions/m1_u1'), { pick: '2' }));
    });

    it('Başkasının tahmini okunamaz', async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await setDoc(doc(ctx.firestore(), 'predictions/m1_u1'), { fixtureId: 'm1', userId: 'u1', pick: '1', status: 'PENDING' });
        });
        await assertFails(getDoc(doc(dbFor('u2'), 'predictions/m1_u1')));
        await assertSucceeds(getDoc(doc(dbFor('u1'), 'predictions/m1_u1')));
    });

    it('Gönderi: sadece beğeni alanları güncellenebilir', async () => {
        await env.withSecurityRulesDisabled(async (ctx) => {
            await setDoc(doc(ctx.firestore(), 'feed_posts/p1'), { text: 'merhaba', authorId: 'u1', likesCount: 0, likedBy: [] });
        });
        const ref = doc(dbFor('u2'), 'feed_posts/p1');
        await assertSucceeds(updateDoc(ref, { likesCount: 1, likedBy: ['u2'] }));
        await assertFails(updateDoc(ref, { text: 'değiştirildi' }));
        await assertFails(updateDoc(ref, { authorId: 'u2' }));
    });

    it('daily_bundles istemciden yazılamaz', async () => {
        await assertFails(setDoc(doc(dbFor('u1'), 'daily_bundles/2026-01-01'), { fixtures: [] }));
    });
});
