# Provider Availability Modal

A modern, responsive modal component for setting provider availability in the Health First Provider Portal.

## Features

- **Wide, horizontal layout** - 80% screen width with 80vh max height
- **Two-column responsive design** - Desktop: 2 columns, Mobile: 1 column
- **Real-time validation** - Using Yup schema validation
- **Auto-save drafts** - Saves form state to localStorage
- **Accessibility** - Full keyboard navigation and screen reader support
- **Modern UI/UX** - Material-UI components with custom styling

## Usage

```tsx
import ProviderAvailabilityModal from './components/ProviderAvailabilityModal';

const MyComponent = () => {
  const [isOpen, setIsOpen] = useState(false);
  
  const handleSave = (availability) => {
    // Handle saving availability data
    console.log('New availability:', availability);
  };

  return (
    <ProviderAvailabilityModal
      open={isOpen}
      onClose={() => setIsOpen(false)}
      onSave={handleSave}
      existingAvailability={[]} // Optional: existing data to edit
    />
  );
};
```

## Props

| Prop | Type | Required | Description |
|------|------|----------|-------------|
| `open` | `boolean` | Yes | Controls modal visibility |
| `onClose` | `() => void` | Yes | Callback when modal closes |
| `onSave` | `(availability: ProviderAvailability[]) => void` | Yes | Callback when form is submitted |
| `existingAvailability` | `ProviderAvailability[]` | No | Existing availability data to edit |

## Data Structure

```typescript
interface ProviderAvailability {
  id: string;
  providerId: string;
  day: 'Monday' | 'Tuesday' | 'Wednesday' | 'Thursday' | 'Friday' | 'Saturday' | 'Sunday';
  isAvailable: boolean;
  startTime: string; // "09:00"
  endTime: string;   // "17:00"
  timezone: string;
  repeatWeekly: boolean;
  createdAt?: string;
  updatedAt?: string;
}
```

## Validation Rules

- At least one day must be selected
- Start time and end time are required
- End time must be after start time
- Timezone is required
- Real-time validation feedback

## Accessibility Features

- Modal closes on Escape key
- Modal closes on overlay click
- Modal closes on X button click
- All form fields are keyboard accessible
- Proper ARIA labels and roles
- Focus management

## Styling

- Uses Material-UI theme system
- Responsive design with breakpoints
- Custom styling for modern appearance
- Consistent with design system
- Hover effects and transitions

## Dependencies

- React Hook Form for form management
- Yup for validation
- Material-UI for components
- React Hot Toast for notifications 