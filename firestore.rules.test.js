const assert = require('assert');
const firebase = require('@firebase/rules-unit-testing');

const PROJECT_ID = 'matchpick-free-tier';

describe('MatchPick Firestore Security Rules', () => {
    it('Kullanıcı kendi puanını doğrudan güncelleyemez (Sadece backend yetkili)', async () => {
        const db = firebase.initializeTestApp({ projectId: PROJECT_ID, auth: { uid: 'user_123' } }).firestore();
        const profile = db.collection('users').document('user_123');
        
        await assert.rejects(
            profile.update({ totalPoints: 99999 }),
            /PERMISSION_DENIED/
        );
    });

    it('Kullanıcı başkası adına tahmin dokümanı oluşturamaz', async () => {
        const db = firebase.initializeTestApp({ projectId: PROJECT_ID, auth: { uid: 'attacker_007' } }).firestore();
        const fakePrediction = db.collection('predictions').document('match_1_victim_999');

        await assert.rejects(
            fakePrediction.set({
                fixtureId: 'match_1',
                userId: 'victim_999',
                pick: '1'
            }),
            /PERMISSION_DENIED/
        );
    });
});
