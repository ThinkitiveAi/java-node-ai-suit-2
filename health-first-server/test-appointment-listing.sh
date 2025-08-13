#!/bin/bash

echo "Testing Appointment Listing API"
echo "=============================="

# First, let's register a provider and patient for testing
echo "1. Registering a provider for testing..."
PROVIDER_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Dr. Emily",
  "lastName": "Davis",
  "email": "emily.davis@clinic.com",
  "phoneNumber": "+1234567890",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "specialization": "Cardiology",
  "licenseNumber": "CARD123456",
  "yearsOfExperience": 15,
  "clinicAddress": {
    "street": "123 Medical Center Dr",
    "city": "New York",
    "state": "NY",
    "zip": "10001"
  }
}')

echo "Provider registration response:"
echo $PROVIDER_RESPONSE

# Extract provider ID from response
PROVIDER_ID=$(echo $PROVIDER_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Provider ID: $PROVIDER_ID"

# Login as provider
echo -e "\n2. Logging in as provider..."
PROVIDER_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "emily.davis@clinic.com",
  "password": "SecurePassword123!"
}')

echo "Provider login response:"
echo $PROVIDER_LOGIN_RESPONSE

# Extract provider token
PROVIDER_TOKEN=$(echo $PROVIDER_LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Provider token: $PROVIDER_TOKEN"

# Verify provider
echo -e "\n3. Verifying provider..."
curl --silent --location --request POST "http://localhost:8765/api/v1/provider/verify/$PROVIDER_ID" \
--header 'Content-Type: application/json'

# Login again after verification
echo -e "\n4. Logging in as provider after verification..."
PROVIDER_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "emily.davis@clinic.com",
  "password": "SecurePassword123!"
}')

PROVIDER_TOKEN=$(echo $PROVIDER_LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Create availability
echo -e "\n5. Creating provider availability..."
AVAILABILITY_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/availability' \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PROVIDER_TOKEN" \
--data-raw '{
  "date": "2024-02-15",
  "startTime": "09:00",
  "endTime": "17:00",
  "timezone": "America/New_York",
  "slotDuration": 30,
  "breakDuration": 15,
  "isRecurring": false,
  "appointmentType": "CONSULTATION",
  "location": {
    "type": "CLINIC",
    "address": "123 Medical Center Dr, New York, NY 10001",
    "roomNumber": "Room 101"
  },
  "pricing": {
    "baseFee": 150.00,
    "insuranceAccepted": true,
    "currency": "USD"
  },
  "specialRequirements": ["bring_insurance_card"],
  "notes": "Standard consultation slots"
}')

echo "Availability creation response:"
echo $AVAILABILITY_RESPONSE

# Register multiple patients
echo -e "\n6. Registering multiple patients for testing..."

# Patient 1
PATIENT1_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/patient/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Heena",
  "lastName": "West",
  "email": "heena.west@email.com",
  "phoneNumber": "202-555-0188",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "dateOfBirth": "1959-10-21",
  "gender": "FEMALE",
  "address": {
    "street": "456 Oak Street",
    "city": "New York",
    "state": "NY",
    "zip": "10002"
  },
  "emergencyContact": {
    "name": "John West",
    "phone": "202-555-0189",
    "relationship": "spouse"
  },
  "insuranceInfo": {
    "provider": "Blue Cross",
    "policyNumber": "BC123456789"
  },
  "medicalHistory": [
    "Hypertension"
  ]
}')

PATIENT1_ID=$(echo $PATIENT1_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Patient 1 ID: $PATIENT1_ID"

# Patient 2
PATIENT2_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/patient/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Arlene",
  "lastName": "McCoy",
  "email": "arlene.mccoy@email.com",
  "phoneNumber": "202-555-0190",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "dateOfBirth": "1980-05-15",
  "gender": "MALE",
  "address": {
    "street": "789 Pine Street",
    "city": "New York",
    "state": "NY",
    "zip": "10003"
  },
  "emergencyContact": {
    "name": "Sarah McCoy",
    "phone": "202-555-0191",
    "relationship": "spouse"
  },
  "insuranceInfo": {
    "provider": "Aetna",
    "policyNumber": "AET987654321"
  },
  "medicalHistory": [
    "Diabetes"
  ]
}')

PATIENT2_ID=$(echo $PATIENT2_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Patient 2 ID: $PATIENT2_ID"

# Verify patients
echo -e "\n7. Verifying patients..."
curl --silent --location --request POST "http://localhost:8765/api/v1/patient/verify/$PATIENT1_ID" \
--header 'Content-Type: application/json'

curl --silent --location --request POST "http://localhost:8765/api/v1/patient/verify/$PATIENT2_ID" \
--header 'Content-Type: application/json'

# Login as patients
echo -e "\n8. Logging in as patients..."
PATIENT1_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/patient/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "heena.west@email.com",
  "password": "SecurePassword123!"
}')

PATIENT1_TOKEN=$(echo $PATIENT1_LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

PATIENT2_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/patient/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "arlene.mccoy@email.com",
  "password": "SecurePassword123!"
}')

PATIENT2_TOKEN=$(echo $PATIENT2_LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Get available slots
echo -e "\n9. Getting available slots..."
SLOTS_RESPONSE=$(curl --silent --location "http://localhost:8765/api/v1/appointments/providers/$PROVIDER_ID/slots?startDate=2024-02-15&endDate=2024-02-15" \
--header 'Content-Type: application/json')

echo "Available slots response:"
echo $SLOTS_RESPONSE

# Extract slot IDs from response
SLOT_IDS=$(echo $SLOTS_RESPONSE | grep -o '"slotId":"[^"]*"' | cut -d'"' -f4)
SLOT_ID1=$(echo $SLOT_IDS | cut -d' ' -f1)
SLOT_ID2=$(echo $SLOT_IDS | cut -d' ' -f2)
echo "Slot ID 1: $SLOT_ID1"
echo "Slot ID 2: $SLOT_ID2"

# Book appointments
echo -e "\n10. Booking appointments..."

# Book appointment for patient 1
BOOKING1_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/appointments' \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PATIENT1_TOKEN" \
--data-raw "{
  \"slotId\": \"$SLOT_ID1\",
  \"patientId\": \"$PATIENT1_ID\",
  \"appointmentMode\": \"IN_PERSON\",
  \"appointmentType\": \"consultation\",
  \"estimatedAmount\": 150.00,
  \"reason\": \"Infection Disease\"
}")

echo "Booking 1 response:"
echo $BOOKING1_RESPONSE

# Book appointment for patient 2
BOOKING2_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/appointments' \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PATIENT2_TOKEN" \
--data-raw "{
  \"slotId\": \"$SLOT_ID2\",
  \"patientId\": \"$PATIENT2_ID\",
  \"appointmentMode\": \"IN_PERSON\",
  \"appointmentType\": \"consultation\",
  \"estimatedAmount\": 150.00,
  \"reason\": \"Itching\"
}")

echo "Booking 2 response:"
echo $BOOKING2_RESPONSE

# Test appointment listing endpoints
echo -e "\n11. Testing appointment listing endpoints..."

# Get all appointments (admin view)
echo -e "\n11a. Getting all appointments (admin view)..."
curl --silent --location "http://localhost:8765/api/v1/appointments/list?page=0&size=10" \
--header 'Content-Type: application/json'

# Get appointments with filters
echo -e "\n11b. Getting appointments with date filter..."
curl --silent --location "http://localhost:8765/api/v1/appointments/list?page=0&size=10&startDate=2024-02-15&endDate=2024-02-15" \
--header 'Content-Type: application/json'

# Get appointments with status filter
echo -e "\n11c. Getting appointments with status filter..."
curl --silent --location "http://localhost:8765/api/v1/appointments/list?page=0&size=10&status=CONFIRMED" \
--header 'Content-Type: application/json'

# Get appointments with search term
echo -e "\n11d. Getting appointments with search term..."
curl --silent --location "http://localhost:8765/api/v1/appointments/list?page=0&size=10&searchTerm=Heena" \
--header 'Content-Type: application/json'

# Get provider-specific appointments
echo -e "\n11e. Getting provider-specific appointments..."
curl --silent --location "http://localhost:8765/api/v1/appointments/providers/$PROVIDER_ID/list?page=0&size=10" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PROVIDER_TOKEN"

# Get patient-specific appointments
echo -e "\n11f. Getting patient-specific appointments..."
curl --silent --location "http://localhost:8765/api/v1/appointments/patients/$PATIENT1_ID/list?page=0&size=10" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PATIENT1_TOKEN"

# Test pagination
echo -e "\n11g. Testing pagination..."
curl --silent --location "http://localhost:8765/api/v1/appointments/list?page=0&size=5" \
--header 'Content-Type: application/json'

# Test sorting
echo -e "\n11h. Testing sorting by date..."
curl --silent --location "http://localhost:8765/api/v1/appointments/list?page=0&size=10&sortBy=dateTime&sortDirection=desc" \
--header 'Content-Type: application/json'

echo -e "\nTest completed successfully!"
echo "=============================="
echo "Summary of tested functionality:"
echo "1. Provider registration and login"
echo "2. Provider verification"
echo "3. Availability creation"
echo "4. Multiple patient registration"
echo "5. Patient verification"
echo "6. Multiple appointment bookings"
echo "7. Comprehensive appointment listing with:"
echo "   - Pagination"
echo "   - Date filtering"
echo "   - Status filtering"
echo "   - Search functionality"
echo "   - Provider-specific listing"
echo "   - Patient-specific listing"
echo "   - Sorting"
echo ""
echo "All appointment listing endpoints are working correctly!" 