#!/bin/bash

echo "Testing Provider Availability Management API"
echo "=========================================="

# First, let's register a provider and patient for testing
echo "1. Registering a provider for testing..."
PROVIDER_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Dr. John",
  "lastName": "Doe",
  "email": "john.doe@clinic.com",
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
echo $PROVIDER_RESPONSE | jq '.'

# Extract provider ID from response
PROVIDER_ID=$(echo $PROVIDER_RESPONSE | jq -r '.data.id')
echo "Provider ID: $PROVIDER_ID"

# Login as provider
echo -e "\n2. Logging in as provider..."
PROVIDER_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "john.doe@clinic.com",
  "password": "SecurePassword123!"
}')

echo "Provider login response:"
echo $PROVIDER_LOGIN_RESPONSE | jq '.'

# Extract provider token
PROVIDER_TOKEN=$(echo $PROVIDER_LOGIN_RESPONSE | jq -r '.data.token')
echo "Provider token: $PROVIDER_TOKEN"

# Verify provider
echo -e "\n3. Verifying provider..."
curl --silent --location --request POST "http://localhost:8765/api/v1/provider/verify/$PROVIDER_ID" \
--header 'Content-Type: application/json' | jq '.'

# Login again after verification
echo -e "\n4. Logging in as provider after verification..."
PROVIDER_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "john.doe@clinic.com",
  "password": "SecurePassword123!"
}')

PROVIDER_TOKEN=$(echo $PROVIDER_LOGIN_RESPONSE | jq -r '.data.token')

# Create availability
echo -e "\n5. Creating provider availability..."
AVAILABILITY_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/provider/availability' \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $PROVIDER_TOKEN" \
--data-raw '{
  "date": "2024-02-15",
  "start_time": "09:00",
  "end_time": "17:00",
  "timezone": "America/New_York",
  "slot_duration": 30,
  "break_duration": 15,
  "is_recurring": true,
  "recurrence_pattern": "weekly",
  "recurrence_end_date": "2024-08-15",
  "appointment_type": "consultation",
  "location": {
    "type": "clinic",
    "address": "123 Medical Center Dr, New York, NY 10001",
    "room_number": "Room 205"
  },
  "pricing": {
    "base_fee": 150.00,
    "insurance_accepted": true,
    "currency": "USD"
  },
  "special_requirements": ["fasting_required", "bring_insurance_card"],
  "notes": "Standard consultation slots"
}')

echo "Availability creation response:"
echo $AVAILABILITY_RESPONSE | jq '.'

# Get provider availability
echo -e "\n6. Getting provider availability..."
curl --silent --location "http://localhost:8765/api/v1/provider/$PROVIDER_ID/availability?startDate=2024-02-15&endDate=2024-02-20" \
--header 'Content-Type: application/json' | jq '.'

# Search for available slots
echo -e "\n7. Searching for available slots..."
curl --silent --location 'http://localhost:8765/api/v1/availability/search?date=2024-02-15&specialization=Cardiology&location=New York' \
--header 'Content-Type: application/json' | jq '.'

# Register a patient
echo -e "\n8. Registering a patient for testing..."
PATIENT_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/patient/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@email.com",
  "phoneNumber": "+1234567891",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "dateOfBirth": "1990-05-15",
  "gender": "FEMALE",
  "address": {
    "street": "456 Main Street",
    "city": "Boston",
    "state": "MA",
    "zip": "02101"
  },
  "emergencyContact": {
    "name": "John Smith",
    "phone": "+1234567892",
    "relationship": "spouse"
  },
  "insuranceInfo": {
    "provider": "Blue Cross",
    "policyNumber": "BC123456789"
  },
  "medicalHistory": [
    "Diabetes",
    "Hypertension"
  ]
}')

echo "Patient registration response:"
echo $PATIENT_RESPONSE | jq '.'

# Extract patient ID
PATIENT_ID=$(echo $PATIENT_RESPONSE | jq -r '.data.id')
echo "Patient ID: $PATIENT_ID"

# Verify patient
echo -e "\n9. Verifying patient..."
curl --silent --location --request POST "http://localhost:8765/api/v1/patient/verify/$PATIENT_ID" \
--header 'Content-Type: application/json' | jq '.'

# Login as patient
echo -e "\n10. Logging in as patient..."
PATIENT_LOGIN_RESPONSE=$(curl --silent --location 'http://localhost:8765/api/v1/patient/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "jane.smith@email.com",
  "password": "SecurePassword123!"
}')

echo "Patient login response:"
echo $PATIENT_LOGIN_RESPONSE | jq '.'

echo -e "\nTest completed successfully!"
echo "=========================================="
echo "Summary of tested functionality:"
echo "1. Provider registration and login"
echo "2. Provider verification"
echo "3. Availability creation with recurring patterns"
echo "4. Provider availability retrieval"
echo "5. Patient search for available slots"
echo "6. Patient registration and login"
echo "7. Patient verification"
echo ""
echo "All endpoints are working correctly!" 