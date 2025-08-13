export interface DayAvailability {
  id: string;
  day: 'Monday' | 'Tuesday' | 'Wednesday' | 'Thursday' | 'Friday' | 'Saturday' | 'Sunday';
  startTime: string; // "09:00"
  endTime: string;   // "17:00"
}

export interface BlockDay {
  id: string;
  date: string; // "2024-01-15"
  startTime: string; // "09:00"
  endTime: string;   // "17:00"
}

export interface ProviderAvailability {
  id: string;
  providerId: string;
  providerName?: string;
  dayAvailabilities: DayAvailability[];
  blockDays: BlockDay[];
  timezone: string;
  repeatWeekly: boolean;
  locationMode?: 'Virtual' | 'In-clinic' | 'Home Visit';
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface AvailabilityFormData {
  providerName: string;
  dayAvailabilities: DayAvailability[];
  blockDays: BlockDay[];
  timezone: string;
  repeatWeekly: boolean;
  locationMode?: 'Virtual' | 'In-clinic' | 'Home Visit';
  notes?: string;
}

export interface AvailabilityModalProps {
  open: boolean;
  onClose: () => void;
  onSave: (availability: ProviderAvailability) => void;
  existingAvailability?: ProviderAvailability;
} 