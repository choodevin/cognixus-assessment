CLIENT_ID="283383699868-kud40qv9qa6qiu474tj6jgpacs472gpg.apps.googleusercontent.com"
CLIENT_SECRET="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"
REDIRECT_URI="http://localhost:8080"

AUTH_URL="https://accounts.google.com/o/oauth2/auth?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code&scope=openid%20profile%20email"

echo "Visit the following URL to authorize the app:"
echo "${AUTH_URL}"

echo "Enter the code obtained after authorization:"
read -r CODE

TOKEN_URL="https://oauth2.googleapis.com/token"
RESPONSE=$(curl -X POST -H "Content-Type: application/x-www-form-urlencoded" \
            -d "code=${CODE}&client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&redirect_uri=${REDIRECT_URI}&grant_type=authorization_code" \
            ${TOKEN_URL})


ACCESS_TOKEN=$(echo "${RESPONSE}" | sed -n 's/.*"access_token": "\(.*\)".*/\1/p')

echo "Access Token: ${ACCESS_TOKEN}"

read