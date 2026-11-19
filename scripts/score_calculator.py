import os
import json
import firebase_admin
from firebase_admin import credentials, firestore

def resolve_matches_and_award_points(finished_date_str):
    db = firestore.client()
    
    # Günün maç paketini oku (1 Read)
    bundle_ref = db.collection("daily_bundles").document(finished_date_str)
    bundle_doc = bundle_ref.get()
    
    if not bundle_doc.exists:
        print("No matches found for evaluation.")
        return
        
    fixtures = bundle_doc.to_dict().get("fixtures", [])
    
    # Sadece tamamlanmış ve skoru netleşmiş maçları filtrele
    # result_pick: "1", "X" veya "2"
    finished_map = {f["id"]: f.get("result_pick") for f in fixtures if f.get("status") == "FINISHED"}
    
    if not finished_map:
        print("No completed matches to grade.")
        return

    # Tahminleri oku (Günün tahminleri)
    predictions_query = db.collection("predictions")\
        .where("status", "==", "PENDING")\
        .limit(400)\
        .stream()

    batch = db.batch()
    writes_count = 0

    for pred in predictions_query:
        p_data = pred.to_dict()
        f_id = p_data.get("fixtureId")
        user_pick = p_data.get("pick")
        u_id = p_data.get("userId")
        
        if f_id in finished_map:
            actual = finished_map[f_id]
            user_ref = db.collection("users").document(u_id)
            pred_ref = db.collection("predictions").document(pred.id)
            
            if user_pick == actual:
                # Kazandı: +10 puan ve streak +1
                batch.update(pred_ref, {"status": "WON"})
                batch.update(user_ref, {
                    "totalPoints": firestore.Increment(10),
                    "currentStreak": firestore.Increment(1)
                })
            else:
                # Kaybetti: Streak sıfırlanır
                batch.update(pred_ref, {"status": "LOST"})
                batch.update(user_ref, {"currentStreak": 0})
                
            writes_count += 2
            
            # Firestore Spark kotası: Her batch en fazla 500 işlem alabilir
            if writes_count >= 400:
                batch.commit()
                batch = db.batch()
                writes_count = 0

    if writes_count > 0:
        batch.commit()
        print(f"Scoring resolved successfully with {writes_count} updates.")

if __name__ == "__main__":
    from datetime import datetime, timezone
    today = datetime.now(timezone.utc).strftime("%Y-%m-%d")
    resolve_matches_and_award_points(today)
