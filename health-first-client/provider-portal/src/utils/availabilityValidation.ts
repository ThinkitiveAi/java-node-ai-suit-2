import * as yup from 'yup';

export const dayAvailabilitySchema = yup.object({
  id: yup.string(),
  day: yup.string().required('Day is required'),
  startTime: yup.string().required('Start time is required'),
  endTime: yup.string().required('End time is required'),
}).test('valid-time-range', 'End time must be after start time', (value) => {
  if (!value.startTime || !value.endTime) return true;
  
  const start = new Date(`2000-01-01T${value.startTime}`);
  const end = new Date(`2000-01-01T${value.endTime}`);
  
  return end > start;
});

export const blockDaySchema = yup.object({
  id: yup.string(),
  date: yup.string().required('Date is required'),
  startTime: yup.string().required('Start time is required'),
  endTime: yup.string().required('End time is required'),
}).test('valid-time-range', 'End time must be after start time', (value) => {
  if (!value.startTime || !value.endTime) return true;
  
  const start = new Date(`2000-01-01T${value.startTime}`);
  const end = new Date(`2000-01-01T${value.endTime}`);
  
  return end > start;
});

export const availabilitySchema = yup.object({
  providerName: yup.string().required('Provider name is required'),
  dayAvailabilities: yup.array().of(dayAvailabilitySchema).min(1, 'At least one day availability is required'),
  blockDays: yup.array().of(blockDaySchema),
  timezone: yup.string().required('Timezone is required'),
  repeatWeekly: yup.boolean(),
  locationMode: yup.string().oneOf(['Virtual', 'In-clinic', 'Home Visit']),
  notes: yup.string(),
});

export const timezones = [
  { value: 'America/New_York', label: 'Eastern Time (ET)' },
  { value: 'America/Chicago', label: 'Central Time (CT)' },
  { value: 'America/Denver', label: 'Mountain Time (MT)' },
  { value: 'America/Los_Angeles', label: 'Pacific Time (PT)' },
  { value: 'America/Anchorage', label: 'Alaska Time (AKT)' },
  { value: 'Pacific/Honolulu', label: 'Hawaii Time (HST)' },
  { value: 'UTC', label: 'UTC' },
  { value: 'Europe/London', label: 'London (GMT)' },
  { value: 'Europe/Paris', label: 'Paris (CET)' },
  { value: 'Asia/Tokyo', label: 'Tokyo (JST)' },
  { value: 'Asia/Shanghai', label: 'Shanghai (CST)' },
  { value: 'Asia/Kolkata', label: 'India (IST)' },
  { value: 'Australia/Sydney', label: 'Sydney (AEST)' },
];

export const daysOfWeek = [
  'Monday',
  'Tuesday', 
  'Wednesday',
  'Thursday',
  'Friday',
  'Saturday',
  'Sunday'
];

export const locationModes = [
  { value: 'Virtual', label: 'Virtual' },
  { value: 'In-clinic', label: 'In-clinic' },
  { value: 'Home Visit', label: 'Home Visit' },
]; 