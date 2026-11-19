import os
import json
from datetime import datetime, timezone
import requests
import firebase_admin
from firebase_admin import credentials, firestore, messaging

# Firebase Admin Başlatma (Güvenli ve Graceful Kontrol)
cred_json = os.environ.get("FIREBASE_SERVICE_ACCOUNT")
if not cred_json or not cred_json.strip():
    print("WARNING: FIREBASE_SERVICE_ACCOUNT secret is not configured in GitHub repository secrets.")
    print("Skipping match sync & push engine gracefully.")
    exit(0)

try:
    cred = credentials.Certificate(json.loads(cred_json))
    firebase_admin.initialize_app(cred)
    db = firestore.client()
except Exception as e:
    print(f"WARNING: Firebase initialization failed: {e}")
    print("Ensure FIREBASE_SERVICE_ACCOUNT is valid service account JSON.")
    exit(0)

TODAY_STR = datetime.now(timezone.utc).strftime("%Y-%m-%d")

def fetch_and_sync_fixtures():
    # Günün derbi maçları veri modeli
    mock_fixtures = [
        {
            "id": f"laliga_{TODAY_STR}_1",
            "leagueId": "laliga",
            "leagueName": "La Liga EA Sports",
            "countryCode": "ES",
            "homeTeamName": "Real Madrid",
            "homeTeamLogo": "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
            "awayTeamName": "FC Barcelona",
            "awayTeamLogo": "https://images.fotmob.com/image_resources/logo/teamlogo/8634.png",
            "matchTimestamp": int(datetime.now(timezone.utc).timestamp()) + 7200,
            "officialPick": "1",
            "officialConfidence": 84,
            "isLocked": False
        },
        {
            "id": f"epl_{TODAY_STR}_1",
            "leagueId": "epl",
            "leagueName": "Premier League",
            "countryCode": "GB",
            "homeTeamName": "Arsenal",
            "homeTeamLogo": "https://images.fotmob.com/image_resources/logo/teamlogo/9825.png",
            "awayTeamName": "Manchester City",
            "awayTeamLogo": "https://images.fotmob.com/image_resources/logo/teamlogo/8456.png",
            "matchTimestamp": int(datetime.now(timezone.utc).timestamp()) + 10800,
            "officialPick": "X",
            "officialConfidence": 79,
            "isLocked": False
        },
        {
            "id": f"superlig_{TODAY_STR}_1",
            "leagueId": "superlig",
            "leagueName": "Trendyol Süper Lig",
            "countryCode": "TR",
            "homeTeamName": "Galatasaray",
            "homeTeamLogo": "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png",
            "awayTeamName": "Fenerbahçe",
            "awayTeamLogo": "https://images.fotmob.com/image_resources/logo/teamlogo/8695.png",
            "matchTimestamp": int(datetime.now(timezone.utc).timestamp()) + 14400,
            "officialPick": "1",
            "officialConfidence": 74,
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
    try:
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
    except Exception as e:
        print(f"FCM notification skipped: {e}")

if __name__ == "__main__":
    fetch_and_sync_fixtures()
    send_free_fcm_notification()
