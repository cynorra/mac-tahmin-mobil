import os
import json
from datetime import datetime, timezone
import requests
import firebase_admin
from firebase_admin import credentials, firestore, messaging

# Firebase Admin Başlatma
cred_json = os.environ.get("FIREBASE_SERVICE_ACCOUNT")
cred = credentials.Certificate(json.loads(cred_json))
firebase_admin.initialize_app(cred)
db = firestore.client()

TODAY_STR = datetime.now(timezone.utc).strftime("%Y-%m-%d")

def fetch_and_sync_fixtures():
    # Örnek dummy fikstür veri modeli (Ücretsiz bir Football API entegre edilir)
    mock_fixtures = [
        {
            "id": f"mls_{TODAY_STR}_1",
            "leagueId": "mls",
            "leagueName": "MLS",
            "countryCode": "US",
            "homeTeamName": "Inter Miami",
            "homeTeamLogo": "https://example.com/miami.png",
            "awayTeamName": "LA Galaxy",
            "awayTeamLogo": "https://example.com/galaxy.png",
            "matchTimestamp": int(datetime.now(timezone.utc).timestamp()) + 7200,
            "officialPick": "1",
            "officialConfidence": 74,
            "isLocked": False
        },
        {
            "id": f"superlig_{TODAY_STR}_1",
            "leagueId": "superlig",
            "leagueName": "Süper Lig",
            "countryCode": "TR",
            "homeTeamName": "Galatasaray",
            "homeTeamLogo": "https://example.com/gs.png",
            "awayTeamName": "Fenerbahçe",
            "awayTeamLogo": "https://example.com/fb.png",
            "matchTimestamp": int(datetime.now(timezone.utc).timestamp()) + 14400,
            "officialPick": "1",
            "officialConfidence": 68,
            "isLocked": False
        }
    ]

    # Firestore Spark kotasını korumak için tek dokümana yazılır
    bundle_ref = db.collection("daily_bundles").document(TODAY_STR)
    bundle_ref.set({
        "date": TODAY_STR,
        "fixtures": mock_fixtures,
        "updatedAt": firestore.SERVER_TIMESTAMP
    }, merge=True)

    print(f"[{TODAY_STR}] Fikstür başarıyla güncellendi (1 write).")

def send_free_fcm_notification():
    # FCM Topic Bildirimi (Tamamen ücretsiz)
    message = messaging.Message(
        data={
            "title": "MatchPick Updates",
            "body": "Today's fixtures & official picks are live! Make your predictions now 🎯",
            "action": "OPEN_PREDICTIONS"
        },
        topic="all_users"
    )
    response = messaging.send(message)
    print(f"FCM Bildirimi Gönderildi: {response}")

if __name__ == "__main__":
    fetch_and_sync_fixtures()
    send_free_fcm_notification()
