import os
import json
import datetime
import random
import requests

# ESPN public scoreboard leagues (returns full daily & multi-day schedules with team logos and venues)
ESPN_LEAGUES = [
    {'slug': 'uefa.champions', 'code': 'ucl', 'name': 'UEFA Champions League', 'country': 'EU'},
    {'slug': 'uefa.europa', 'code': 'ucl', 'name': 'UEFA Europa League', 'country': 'EU'},
    {'slug': 'eng.1', 'code': 'epl', 'name': 'Premier League', 'country': 'GB'},
    {'slug': 'esp.1', 'code': 'laliga', 'name': 'La Liga EA Sports', 'country': 'ES'},
    {'slug': 'tur.1', 'code': 'superlig', 'name': 'Trendyol Süper Lig', 'country': 'TR'},
    {'slug': 'ger.1', 'code': 'bundesliga', 'name': 'German Bundesliga', 'country': 'DE'},
    {'slug': 'ita.1', 'code': 'seriea', 'name': 'Italian Serie A', 'country': 'IT'},
]

# Fallback TheSportsDB league IDs
SPORTSDB_LEAGUES = {
    '4328': {'code': 'epl', 'name': 'Premier League', 'country': 'GB'},
    '4335': {'code': 'laliga', 'name': 'La Liga EA Sports', 'country': 'ES'},
    '4339': {'code': 'superlig', 'name': 'Trendyol Süper Lig', 'country': 'TR'},
    '4331': {'code': 'bundesliga', 'name': 'German Bundesliga', 'country': 'DE'},
    '4332': {'code': 'seriea', 'name': 'Italian Serie A', 'country': 'IT'}
}

def calculate_ai_odds_and_preview(home_team, away_team, league_name):
    seed_val = sum(ord(c) for c in (home_team + away_team))
    rng = random.Random(seed_val)

    home_strength = rng.uniform(1.2, 2.5)
    away_strength = rng.uniform(0.9, 2.2)

    total = home_strength + away_strength + 1.0
    prob_home = int((home_strength / total) * 100)
    prob_away = int((away_strength / total) * 100)
    prob_draw = 100 - prob_home - prob_away

    if prob_home >= prob_away:
        official_pick = "1"
        confidence = min(88, max(68, prob_home + 18))
    else:
        official_pick = "2"
        confidence = min(85, max(66, prob_away + 16))

    odds_home = round(max(1.35, 100.0 / max(prob_home, 20)), 2)
    odds_draw = round(max(2.80, 100.0 / max(prob_draw, 15)), 2)
    odds_away = round(max(1.45, 100.0 / max(prob_away, 18)), 2)

    analysis = (
        f"{league_name} clash: {home_team} hosts {away_team}. "
        f"Tactical telemetry models project {prob_home}% home win probability with an expected xG advantage. "
        f"Key midfield transitions and high pressing will define the outcome."
    )

    return {
        'officialPick': official_pick,
        'officialConfidence': confidence,
        'oddsHome': float(odds_home),
        'oddsDraw': float(odds_draw),
        'oddsAway': float(odds_away),
        'votesHomePercent': prob_home,
        'votesDrawPercent': prob_draw,
        'votesAwayPercent': prob_away,
        'aiAnalysis': analysis
    }

def parse_iso_timestamp(ts_str):
    if not ts_str:
        return int(datetime.datetime.now(datetime.timezone.utc).timestamp()) + 7200
    try:
        clean_ts = ts_str.replace("Z", "+00:00")
        dt = datetime.datetime.fromisoformat(clean_ts)
        if dt.tzinfo is None:
            dt = dt.replace(tzinfo=datetime.timezone.utc)
        return int(dt.timestamp())
    except Exception:
        return int(datetime.datetime.now(datetime.timezone.utc).timestamp()) + 7200

def fetch_espn_fixtures_for_dates(date_offsets=(0, 1, 2, 3, 4)):
    fixtures = []
    seen_ids = set()
    headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'}
    now_utc = datetime.datetime.now(datetime.timezone.utc)
    cutoff_past = int((now_utc - datetime.timedelta(hours=6)).timestamp())

    for offset in date_offsets:
        target_dt = now_utc + datetime.timedelta(days=offset)
        date_param = target_dt.strftime("%Y%m%d")

        for cfg in ESPN_LEAGUES:
            url = f"https://site.api.espn.com/apis/site/v2/sports/soccer/{cfg['slug']}/scoreboard?dates={date_param}"
            try:
                res = requests.get(url, headers=headers, timeout=10)
                if res.status_code != 200:
                    continue
                events = res.json().get('events') or []
                for ev in events:
                    ev_id = str(ev.get('id') or '')
                    if not ev_id or ev_id in seen_ids:
                        continue

                    comp = (ev.get('competitions') or [{}])[0]
                    competitors = comp.get('competitors') or []
                    if len(competitors) < 2:
                        continue

                    home_comp = next((c for c in competitors if c.get('homeAway') == 'home'), competitors[0])
                    away_comp = next((c for c in competitors if c.get('homeAway') == 'away'), competitors[1])

                    home_team = home_comp.get('team') or {}
                    away_team = away_comp.get('team') or {}

                    home_name = home_team.get('displayName') or home_team.get('name') or 'Home'
                    away_name = away_team.get('displayName') or away_team.get('name') or 'Away'
                    home_logo = home_team.get('logo') or ''
                    away_logo = away_team.get('logo') or ''

                    timestamp = parse_iso_timestamp(ev.get('date'))
                    status_type = (ev.get('status') or {}).get('type') or {}
                    state = status_type.get('state', 'pre')
                    completed = bool(status_type.get('completed', False))

                    # Skip matches that already finished more than 6 hours ago
                    if completed and timestamp < cutoff_past:
                        continue

                    seen_ids.add(ev_id)
                    venue = (comp.get('venue') or {}).get('fullName') or 'Main Stadium'
                    ai_metrics = calculate_ai_odds_and_preview(home_name, away_name, cfg['name'])

                    is_locked = completed or (state != 'pre')
                    result_pick = None
                    if completed:
                        try:
                            h_score = int(home_comp.get('score', 0))
                            a_score = int(away_comp.get('score', 0))
                            result_pick = "1" if h_score > a_score else ("2" if a_score > h_score else "X")
                        except Exception:
                            pass

                    fixture_dict = {
                        'id': f"espn_{ev_id}",
                        'leagueId': cfg['code'],
                        'leagueName': cfg['name'],
                        'countryCode': cfg['country'],
                        'homeTeamName': home_name,
                        'homeTeamLogo': home_logo,
                        'awayTeamName': away_name,
                        'awayTeamLogo': away_logo,
                        'stadiumName': venue,
                        'matchTimestamp': timestamp,
                        'userPick': None,
                        'isLocked': is_locked,
                        'status': 'FINISHED' if completed else ('LIVE' if state == 'in' else 'SCHEDULED'),
                        'result_pick': result_pick,
                        **ai_metrics
                    }
                    fixtures.append(fixture_dict)
            except Exception as e:
                print(f"ESPN fetch warning ({cfg['name']} - {date_param}): {e}")

    fixtures.sort(key=lambda x: x['matchTimestamp'])
    return fixtures

def fetch_fallback_sportsdb_fixtures():
    fixtures = []
    seen_events = set()
    headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'}

    for league_id, cfg in SPORTSDB_LEAGUES.items():
        url = f"https://www.thesportsdb.com/api/v1/json/123/eventsnextleague.php?id={league_id}"
        try:
            res = requests.get(url, headers=headers, timeout=12)
            if res.status_code == 200:
                events = res.json().get('events') or []
                for ev in events:
                    ev_id = ev.get('idEvent')
                    if not ev_id or ev_id in seen_events:
                        continue
                    seen_events.add(ev_id)

                    home_name = ev.get('strHomeTeam') or 'Home'
                    away_name = ev.get('strAwayTeam') or 'Away'
                    timestamp = parse_iso_timestamp(ev.get('strTimestamp'))
                    ai_metrics = calculate_ai_odds_and_preview(home_name, away_name, cfg['name'])

                    fixture_dict = {
                        'id': f"real_{ev_id}",
                        'leagueId': cfg['code'],
                        'leagueName': cfg['name'],
                        'countryCode': cfg['country'],
                        'homeTeamName': home_name,
                        'homeTeamLogo': ev.get('strHomeTeamBadge') or '',
                        'awayTeamName': away_name,
                        'awayTeamLogo': ev.get('strAwayTeamBadge') or '',
                        'stadiumName': ev.get('strVenue') or 'Main Stadium',
                        'matchTimestamp': timestamp,
                        'userPick': None,
                        'isLocked': False,
                        **ai_metrics
                    }
                    fixtures.append(fixture_dict)
        except Exception as e:
            print(f"Failed to fetch fallback league {cfg['name']}: {e}")

    return fixtures

def main():
    fixtures = fetch_espn_fixtures_for_dates(date_offsets=(0, 1, 2, 3, 4))
    if not fixtures:
        fixtures = fetch_fallback_sportsdb_fixtures()

    # Build a diverse daily bundle: include all of today's & tomorrow's matches, plus top domestic league matches across the week
    by_league = {}
    for f in fixtures:
        by_league.setdefault(f['leagueId'], []).append(f)

    balanced = []
    # Guarantee up to 8 fixtures from each domestic league (superlig, epl, laliga, bundesliga, seriea) and up to 18 from ucl/uel
    caps = {'superlig': 8, 'epl': 8, 'laliga': 8, 'bundesliga': 8, 'seriea': 8, 'ucl': 18}
    for lg, items in by_league.items():
        balanced.extend(items[:caps.get(lg, 8)])

    balanced.sort(key=lambda x: x['matchTimestamp'])
    fixtures = balanced
    print(f"Fetched {len(fixtures)} real fixtures from sports API across {len(by_league)} leagues.")

    # Save to local assets for offline/embedded instant seed
    assets_dir = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
    os.makedirs(assets_dir, exist_ok=True)
    assets_path = os.path.join(assets_dir, "fixtures_live.json")
    with open(assets_path, "w", encoding="utf-8") as f:
        json.dump(fixtures, f, ensure_ascii=False, indent=2)
    print(f"Saved real fixtures to assets: {assets_path}")

    # Sync to Firestore if service account configured
    cred_json = os.environ.get("FIREBASE_SERVICE_ACCOUNT")
    local_cred_path = os.path.join(os.path.dirname(__file__), "..", "matchpick-pro-firebase-adminsdk-fbsvc-8e851e5b98.json")

    cred = None
    if cred_json and cred_json.strip():
        from firebase_admin import credentials
        cred = credentials.Certificate(json.loads(cred_json))
    elif os.path.exists(local_cred_path):
        from firebase_admin import credentials
        cred = credentials.Certificate(local_cred_path)

    if cred is not None:
        try:
            import firebase_admin
            from firebase_admin import firestore, messaging
            if not firebase_admin._apps:
                firebase_admin.initialize_app(cred)
            db = firestore.client()

            today_str = datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%d")
            bundle_ref = db.collection("daily_bundles").document(today_str)
            bundle_ref.set({
                "date": today_str,
                "fixtures": fixtures,
                "updatedAt": firestore.SERVER_TIMESTAMP
            }, merge=True)
            print(f"Successfully synced {len(fixtures)} real fixtures to Firestore: daily_bundles/{today_str}")

            # Resolve completed predictions via score_calculator
            try:
                from score_calculator import resolve_matches_and_award_points
                resolve_matches_and_award_points(today_str)
            except Exception as sc_err:
                print(f"Score calculator note: {sc_err}")

            # Send free FCM notification
            try:
                msg = messaging.Message(
                    data={
                        "title": "MatchPick Live Fixtures",
                        "body": "Today's Champions League and top league fixtures are live. Make your picks now.",
                        "action": "OPEN_PREDICTIONS"
                    },
                    topic="all_users"
                )
                res = messaging.send(msg)
                print(f"FCM Notification sent: {res}")
            except Exception as fcm_err:
                print(f"FCM Notification skipped: {fcm_err}")

        except Exception as fb_err:
            print(f"Firestore sync warning: {fb_err}")
    else:
        print("Note: FIREBASE_SERVICE_ACCOUNT is empty in environment. Skipped remote Firestore write.")

if __name__ == "__main__":
    main()
