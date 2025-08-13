import React, { useEffect, useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Grid,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Typography,
  IconButton,
  Switch,
  FormControlLabel,
  Autocomplete,
} from '@mui/material';
import {
  Close as CloseIcon,
  Person as PersonIcon,
} from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import toast from 'react-hot-toast';
import { appointmentSchema, appointmentTypes, locations, appointmentStatuses } from '../../utils/appointmentValidation';
import { Appointment, AppointmentFormData, BookAppointmentModalProps, Patient, Provider } from '../../types/appointment';

const BookAppointmentModal: React.FC<BookAppointmentModalProps> = ({
  open,
  onClose,
  onSave,
  existingAppointment,
  patients = [],
  providers = [],
}) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [selectedProvider, setSelectedProvider] = useState<Provider | null>(null);

  const {
    control,
    handleSubmit,
    reset,
    watch,
    formState: { errors, isValid },
    setValue,
  } = useForm<AppointmentFormData>({
    resolver: yupResolver(appointmentSchema),
    defaultValues: {
      patientId: '',
      providerId: '',
      appointmentType: '',
      location: 'In-Clinic',
      date: '',
      startTime: '',
      endTime: '',
      notes: '',
      status: 'Scheduled',
      isRecurring: false,
      recurrenceRule: '',
    },
    mode: 'onChange',
  });

  const watchedIsRecurring = watch('isRecurring');

  // Auto-save draft to localStorage
  useEffect(() => {
    const subscription = watch((value) => {
      localStorage.setItem('appointment-draft', JSON.stringify(value));
    });
    return () => subscription.unsubscribe();
  }, [watch]);

  // Load draft from localStorage on mount
  useEffect(() => {
    const draft = localStorage.getItem('appointment-draft');
    if (draft) {
      try {
        const parsedDraft = JSON.parse(draft);
        reset(parsedDraft);
        
        if (parsedDraft.patientId) {
          const patient = patients.find(p => p.id === parsedDraft.patientId);
          setSelectedPatient(patient || null);
        }
        if (parsedDraft.providerId) {
          const provider = providers.find(p => p.id === parsedDraft.providerId);
          setSelectedProvider(provider || null);
        }
      } catch (error) {
        console.error('Error loading draft:', error);
      }
    }
  }, [reset, patients, providers]);

  // Load existing appointment if provided
  useEffect(() => {
    if (existingAppointment) {
      const formData: AppointmentFormData = {
        patientId: existingAppointment.patientId,
        providerId: existingAppointment.providerId,
        appointmentType: existingAppointment.appointmentType,
        location: existingAppointment.location,
        date: existingAppointment.date,
        startTime: existingAppointment.startTime,
        endTime: existingAppointment.endTime,
        notes: existingAppointment.notes || '',
        status: existingAppointment.status,
        isRecurring: existingAppointment.isRecurring,
        recurrenceRule: existingAppointment.recurrenceRule || '',
      };
      reset(formData);
      
      const patient = patients.find(p => p.id === existingAppointment.patientId);
      const provider = providers.find(p => p.id === existingAppointment.providerId);
      setSelectedPatient(patient || null);
      setSelectedProvider(provider || null);
    }
  }, [existingAppointment, reset, patients, providers]);

  const handleClose = () => {
    localStorage.removeItem('appointment-draft');
    reset();
    setSelectedPatient(null);
    setSelectedProvider(null);
    onClose();
  };

  const onSubmit = async (data: AppointmentFormData) => {
    setIsSubmitting(true);
    
    try {
      const appointment: Appointment = {
        id: existingAppointment?.id || `appointment-${Date.now()}`,
        patientId: data.patientId,
        providerId: data.providerId,
        appointmentType: data.appointmentType,
        location: data.location,
        date: data.date,
        startTime: data.startTime,
        endTime: data.endTime,
        notes: data.notes,
        status: data.status,
        isRecurring: data.isRecurring,
        recurrenceRule: data.recurrenceRule,
        createdAt: existingAppointment?.createdAt || new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      await onSave(appointment);
      
      localStorage.removeItem('appointment-draft');
      toast.success(existingAppointment ? 'Appointment updated successfully!' : 'Appointment booked successfully!');
      handleClose();
    } catch (error) {
      console.error('Error saving appointment:', error);
      toast.error('Failed to save appointment. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="lg"
      fullWidth
      PaperProps={{
        sx: {
          width: '80%',
          maxHeight: '80vh',
          borderRadius: '4px',
          boxShadow: '1px 1px 8px 0px rgba(0, 0, 0, 0.25)',
        },
      }}
    >
      {/* Header */}
      <DialogTitle
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          pb: 1,
          px: 7,
          pt: 4,
          borderBottom: '1px solid #E7E7E7',
        }}
      >
        <Typography variant="h5" component="h2" sx={{ fontWeight: 600 }}>
          Book Appointment
        </Typography>
        <IconButton onClick={handleClose}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent sx={{ px: 7, py: 4 }}>
        <Box component="form" onSubmit={handleSubmit(onSubmit)}>
          <Grid container spacing={5}>
            {/* Left Column */}
            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 500 }}>
                Patient & Provider Information
              </Typography>
              
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                {/* Patient Selection */}
                <Box>
                  <Typography variant="subtitle2" gutterBottom sx={{ fontWeight: 500 }}>
                    Patient Name *
                  </Typography>
                  <Autocomplete
                    options={patients}
                    getOptionLabel={(option) => option.name}
                    value={selectedPatient}
                    onChange={(_, newValue) => {
                      setSelectedPatient(newValue);
                      setValue('patientId', newValue?.id || '');
                    }}
                    renderInput={(params) => (
                      <TextField
                        {...params}
                        placeholder="Select patient"
                        error={!!errors.patientId}
                        helperText={errors.patientId?.message}
                        sx={{
                          '& .MuiOutlinedInput-root': {
                            borderRadius: 2,
                            boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                          },
                        }}
                      />
                    )}
                  />
                </Box>

                {/* Provider Selection */}
                <Box>
                  <Typography variant="subtitle2" gutterBottom sx={{ fontWeight: 500 }}>
                    Provider Name *
                  </Typography>
                  <Autocomplete
                    options={providers}
                    getOptionLabel={(option) => option.name}
                    value={selectedProvider}
                    onChange={(_, newValue) => {
                      setSelectedProvider(newValue);
                      setValue('providerId', newValue?.id || '');
                    }}
                    renderInput={(params) => (
                      <TextField
                        {...params}
                        placeholder="Select provider"
                        error={!!errors.providerId}
                        helperText={errors.providerId?.message}
                        sx={{
                          '& .MuiOutlinedInput-root': {
                            borderRadius: 2,
                            boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                          },
                        }}
                      />
                    )}
                  />
                </Box>

                {/* Appointment Type */}
                <Controller
                  name="appointmentType"
                  control={control}
                  render={({ field }) => (
                    <FormControl fullWidth error={!!errors.appointmentType}>
                      <InputLabel>Appointment Type *</InputLabel>
                      <Select
                        {...field}
                        label="Appointment Type *"
                        sx={{
                          borderRadius: 2,
                          boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                        }}
                      >
                        {appointmentTypes.map((type) => (
                          <MenuItem key={type.value} value={type.value}>
                            {type.label}
                          </MenuItem>
                        ))}
                      </Select>
                      {errors.appointmentType && (
                        <Typography color="error" variant="body2" sx={{ mt: 0.5 }}>
                          {errors.appointmentType.message}
                        </Typography>
                      )}
                    </FormControl>
                  )}
                />

                {/* Location */}
                <Controller
                  name="location"
                  control={control}
                  render={({ field }) => (
                    <FormControl fullWidth error={!!errors.location}>
                      <InputLabel>Location *</InputLabel>
                      <Select
                        {...field}
                        label="Location *"
                        sx={{
                          borderRadius: 2,
                          boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                        }}
                      >
                        {locations.map((location) => (
                          <MenuItem key={location.value} value={location.value}>
                            {location.label}
                          </MenuItem>
                        ))}
                      </Select>
                      {errors.location && (
                        <Typography color="error" variant="body2" sx={{ mt: 0.5 }}>
                          {errors.location.message}
                        </Typography>
                      )}
                    </FormControl>
                  )}
                />

                {/* Status */}
                <Controller
                  name="status"
                  control={control}
                  render={({ field }) => (
                    <FormControl fullWidth error={!!errors.status}>
                      <InputLabel>Status *</InputLabel>
                      <Select
                        {...field}
                        label="Status *"
                        sx={{
                          borderRadius: 2,
                          boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                        }}
                      >
                        {appointmentStatuses.map((status) => (
                          <MenuItem key={status.value} value={status.value}>
                            {status.label}
                          </MenuItem>
                        ))}
                      </Select>
                      {errors.status && (
                        <Typography color="error" variant="body2" sx={{ mt: 0.5 }}>
                          {errors.status.message}
                        </Typography>
                      )}
                    </FormControl>
                  )}
                />
              </Box>
            </Grid>

            {/* Right Column */}
            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 500 }}>
                Schedule & Details
              </Typography>

              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                {/* Date */}
                <Controller
                  name="date"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      type="date"
                      label="Appointment Date *"
                      InputLabelProps={{ shrink: true }}
                      error={!!errors.date}
                      helperText={errors.date?.message}
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          borderRadius: 2,
                          boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                        },
                      }}
                    />
                  )}
                />

                {/* Time Range */}
                <Box>
                  <Typography variant="subtitle2" gutterBottom sx={{ fontWeight: 500 }}>
                    Time Range *
                  </Typography>
                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      <Controller
                        name="startTime"
                        control={control}
                        render={({ field }) => (
                          <TextField
                            {...field}
                            type="time"
                            label="Start Time"
                            InputLabelProps={{ shrink: true }}
                            error={!!errors.startTime}
                            helperText={errors.startTime?.message}
                            sx={{
                              '& .MuiOutlinedInput-root': {
                                borderRadius: 2,
                                boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                              },
                            }}
                          />
                        )}
                      />
                    </Grid>
                    <Grid item xs={6}>
                      <Controller
                        name="endTime"
                        control={control}
                        render={({ field }) => (
                          <TextField
                            {...field}
                            type="time"
                            label="End Time"
                            InputLabelProps={{ shrink: true }}
                            error={!!errors.endTime}
                            helperText={errors.endTime?.message}
                            sx={{
                              '& .MuiOutlinedInput-root': {
                                borderRadius: 2,
                                boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                              },
                            }}
                          />
                        )}
                      />
                    </Grid>
                  </Grid>
                </Box>

                {/* Recurring Toggle */}
                <FormControlLabel
                  control={
                    <Controller
                      name="isRecurring"
                      control={control}
                      render={({ field }) => (
                        <Switch
                          {...field}
                          checked={field.value}
                          sx={{
                            '& .MuiSwitch-switchBase.Mui-checked': {
                              color: 'primary.main',
                            },
                            '& .MuiSwitch-switchBase.Mui-checked + .MuiSwitch-track': {
                              backgroundColor: 'primary.main',
                            },
                          }}
                        />
                      )}
                    />
                  }
                  label={
                    <Box>
                      <Typography variant="subtitle2" sx={{ fontWeight: 500 }}>
                        Recurring Appointment
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Schedule multiple appointments
                      </Typography>
                    </Box>
                  }
                  sx={{ margin: 0 }}
                />

                {/* Notes */}
                <Controller
                  name="notes"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Notes"
                      multiline
                      rows={3}
                      placeholder="Additional notes about the appointment..."
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          borderRadius: 2,
                          boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                        },
                      }}
                    />
                  )}
                />
              </Box>
            </Grid>
          </Grid>
        </Box>
      </DialogContent>

      {/* Footer */}
      <DialogActions
        sx={{
          px: 7,
          pb: 4,
          pt: 2,
          borderTop: '1px solid #E7E7E7',
          gap: 2,
        }}
      >
        <Button
          onClick={handleClose}
          variant="outlined"
          sx={{
            borderRadius: 2,
            px: 4,
            py: 1.5,
            textTransform: 'none',
            fontWeight: 500,
            borderColor: '#233853',
            color: '#233853',
          }}
        >
          Cancel
        </Button>
        <Button
          onClick={handleSubmit(onSubmit)}
          variant="contained"
          disabled={!isValid || isSubmitting}
          sx={{
            borderRadius: 2,
            px: 4,
            py: 1.5,
            textTransform: 'none',
            fontWeight: 500,
            backgroundColor: '#233853',
            '&:hover': {
              backgroundColor: '#1a2a3f',
            },
            '&:disabled': {
              backgroundColor: '#ccc',
            },
          }}
        >
          {isSubmitting ? 'Booking...' : (existingAppointment ? 'Update' : 'Book')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default BookAppointmentModal; 