import React, { useEffect, useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Typography,
  IconButton,
  Switch,
  FormControlLabel,
} from '@mui/material';
import {
  Close as CloseIcon,
  Delete as DeleteIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import { useForm, Controller, useFieldArray } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import toast from 'react-hot-toast';
import { availabilitySchema, timezones, daysOfWeek, locationModes } from '../../utils/availabilityValidation';
import { ProviderAvailability, AvailabilityFormData, AvailabilityModalProps, DayAvailability, BlockDay } from '../../types/availability';

const ProviderAvailabilityModal: React.FC<AvailabilityModalProps> = ({
  open,
  onClose,
  onSave,
  existingAvailability,
}) => {
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    control,
    handleSubmit,
    reset,
    watch,
    formState: { errors, isValid },
  } = useForm<AvailabilityFormData>({
    resolver: yupResolver(availabilitySchema),
    defaultValues: {
      providerName: '',
      dayAvailabilities: [],
      blockDays: [],
      timezone: 'America/New_York',
      repeatWeekly: true,
      locationMode: 'In-clinic',
      notes: '',
    },
    mode: 'onChange',
  });

  const { fields: dayFields, append: appendDay, remove: removeDay } = useFieldArray({
    control,
    name: 'dayAvailabilities',
  });

  const { fields: blockFields, append: appendBlock, remove: removeBlock } = useFieldArray({
    control,
    name: 'blockDays',
  });

  // Auto-save draft to localStorage
  useEffect(() => {
    const subscription = watch((value) => {
      localStorage.setItem('availability-draft', JSON.stringify(value));
    });
    return () => subscription.unsubscribe();
  }, [watch]);

  // Load draft from localStorage on mount
  useEffect(() => {
    const draft = localStorage.getItem('availability-draft');
    if (draft) {
      try {
        const parsedDraft = JSON.parse(draft);
        reset(parsedDraft);
      } catch (error) {
        console.error('Error loading draft:', error);
      }
    }
  }, [reset]);

  // Load existing availability if provided
  useEffect(() => {
    if (existingAvailability) {
      const formData: AvailabilityFormData = {
        providerName: existingAvailability.providerName || '',
        dayAvailabilities: existingAvailability.dayAvailabilities || [],
        blockDays: existingAvailability.blockDays || [],
        timezone: existingAvailability.timezone || 'America/New_York',
        repeatWeekly: existingAvailability.repeatWeekly || true,
        locationMode: existingAvailability.locationMode || 'In-clinic',
        notes: existingAvailability.notes || '',
      };
      reset(formData);
    }
  }, [existingAvailability, reset]);

  const handleClose = () => {
    localStorage.removeItem('availability-draft');
    reset();
    onClose();
  };

  const addDayAvailability = () => {
    const newDay: DayAvailability = {
      id: `day-${Date.now()}`,
      day: 'Monday',
      startTime: '09:00',
      endTime: '17:00',
    };
    appendDay(newDay);
  };

  const addBlockDay = () => {
    const newBlock: BlockDay = {
      id: `block-${Date.now()}`,
      date: new Date().toISOString().split('T')[0],
      startTime: '09:00',
      endTime: '17:00',
    };
    appendBlock(newBlock);
  };

  const onSubmit = async (data: AvailabilityFormData) => {
    setIsSubmitting(true);
    
    try {
      const availability: ProviderAvailability = {
        id: existingAvailability?.id || `availability-${Date.now()}`,
        providerId: 'current-provider',
        providerName: data.providerName,
        dayAvailabilities: data.dayAvailabilities,
        blockDays: data.blockDays,
        timezone: data.timezone,
        repeatWeekly: data.repeatWeekly,
        locationMode: data.locationMode,
        notes: data.notes,
        createdAt: existingAvailability?.createdAt || new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      await onSave(availability);
      
      localStorage.removeItem('availability-draft');
      toast.success('Availability saved successfully!');
      handleClose();
    } catch (error) {
      console.error('Error saving availability:', error);
      toast.error('Failed to save availability. Please try again.');
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
          px: 2,
          pt: 2,
          borderBottom: '1px solid #E7E7E7',
        }}
      >
        <Typography variant="h5" component="h2" sx={{ fontWeight: 600 }}>
          Set Provider Availability
        </Typography>
        <IconButton onClick={handleClose}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent sx={{ px: 2, py: 2 }}>
        <Box component="form" onSubmit={handleSubmit(onSubmit)}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
            
            {/* Provider Selection */}
            <Box sx={{ width: '401px' }}>
              <Controller
                name="providerName"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Provider"
                    placeholder="Select Provider Name"
                    error={!!errors.providerName}
                    helperText={errors.providerName?.message}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        borderRadius: 1,
                        boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                      },
                    }}
                  />
                )}
              />
            </Box>

            {/* Main Content - Two Column Layout */}
            <Box sx={{ display: 'flex', gap: 7, mt: 1.5 }}>
              
              {/* Left Column - Day Wise Availability */}
              <Box sx={{ flex: 1 }}>
                <Typography variant="h6" sx={{ mb: 2, fontWeight: 500, color: '#21262B' }}>
                  Day Wise Availability
                </Typography>
                
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                  {dayFields.map((field, index) => (
                    <Box key={field.id} sx={{ display: 'flex', gap: 2, alignItems: 'flex-end' }}>
                      {/* Day Selection */}
                      <Controller
                        name={`dayAvailabilities.${index}.day`}
                        control={control}
                        render={({ field }) => (
                          <FormControl sx={{ minWidth: 120 }}>
                            <InputLabel>Day</InputLabel>
                            <Select
                              {...field}
                              label="Day"
                              sx={{
                                borderRadius: 1,
                                boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                              }}
                            >
                              {daysOfWeek.map((day) => (
                                <MenuItem key={day} value={day}>
                                  {day}
                                </MenuItem>
                              ))}
                            </Select>
                          </FormControl>
                        )}
                      />

                      {/* Start Time */}
                      <Controller
                        name={`dayAvailabilities.${index}.startTime`}
                        control={control}
                        render={({ field }) => (
                          <TextField
                            {...field}
                            type="time"
                            label="From"
                            InputLabelProps={{ shrink: true }}
                            sx={{
                              minWidth: 120,
                              '& .MuiOutlinedInput-root': {
                                borderRadius: 1,
                                boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                              },
                            }}
                          />
                        )}
                      />

                      {/* End Time */}
                      <Controller
                        name={`dayAvailabilities.${index}.endTime`}
                        control={control}
                        render={({ field }) => (
                          <TextField
                            {...field}
                            type="time"
                            label="Till"
                            InputLabelProps={{ shrink: true }}
                            sx={{
                              minWidth: 120,
                              '& .MuiOutlinedInput-root': {
                                borderRadius: 1,
                                boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                              },
                            }}
                          />
                        )}
                      />

                      {/* Delete Button */}
                      <IconButton
                        onClick={() => removeDay(index)}
                        sx={{
                          backgroundColor: 'white',
                          borderRadius: '10px',
                          boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                          '&:hover': {
                            backgroundColor: 'error.50',
                          },
                        }}
                      >
                        <DeleteIcon sx={{ color: '#373E41' }} />
                      </IconButton>
                    </Box>
                  ))}

                  {/* Add Day Button */}
                  <Button
                    variant="outlined"
                    startIcon={<AddIcon />}
                    onClick={addDayAvailability}
                    sx={{
                      alignSelf: 'flex-start',
                      borderRadius: 1,
                      borderColor: '#233853',
                      color: '#233853',
                      backgroundColor: '#E5F0FF',
                      '&:hover': {
                        backgroundColor: '#D0E0FF',
                        borderColor: '#233853',
                      },
                    }}
                  >
                    Add Day
                  </Button>
                </Box>
              </Box>

              {/* Right Column - Slot Creation Settings */}
              <Box sx={{ flex: 1 }}>
                <Typography variant="h6" sx={{ mb: 2, fontWeight: 500, color: '#21262B' }}>
                  Slot Creation Setting
                </Typography>

                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                  
                  {/* Timezone */}
                  <Controller
                    name="timezone"
                    control={control}
                    render={({ field }) => (
                      <FormControl fullWidth error={!!errors.timezone}>
                        <InputLabel>Time Zone</InputLabel>
                        <Select
                          {...field}
                          label="Time Zone"
                          sx={{
                            borderRadius: 1,
                            boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                          }}
                        >
                          {timezones.map((tz) => (
                            <MenuItem key={tz.value} value={tz.value}>
                              {tz.label}
                            </MenuItem>
                          ))}
                        </Select>
                        {errors.timezone && (
                          <Typography color="error" variant="body2" sx={{ mt: 0.5 }}>
                            {errors.timezone.message}
                          </Typography>
                        )}
                      </FormControl>
                    )}
                  />

                  {/* Location Mode */}
                  <Controller
                    name="locationMode"
                    control={control}
                    render={({ field }) => (
                      <FormControl fullWidth>
                        <InputLabel>Location / Mode</InputLabel>
                        <Select
                          {...field}
                          label="Location / Mode"
                          sx={{
                            borderRadius: 1,
                            boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                          }}
                        >
                          {locationModes.map((mode) => (
                            <MenuItem key={mode.value} value={mode.value}>
                              {mode.label}
                            </MenuItem>
                          ))}
                        </Select>
                      </FormControl>
                    )}
                  />

                  {/* Repeat Weekly */}
                  <FormControlLabel
                    control={
                      <Controller
                        name="repeatWeekly"
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
                          Repeat Weekly
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          This schedule will repeat every week
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
                        placeholder="Additional notes..."
                        sx={{
                          '& .MuiOutlinedInput-root': {
                            borderRadius: 1,
                            boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                          },
                        }}
                      />
                    )}
                  />
                </Box>

                {/* Block Days Section */}
                <Box sx={{ mt: 4 }}>
                  <Typography variant="h6" sx={{ mb: 2, fontWeight: 500, color: '#373D41' }}>
                    Block Days
                  </Typography>
                  
                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                    {blockFields.map((field, index) => (
                      <Box key={field.id} sx={{ display: 'flex', gap: 2, alignItems: 'flex-end' }}>
                        {/* Date */}
                        <Controller
                          name={`blockDays.${index}.date`}
                          control={control}
                          render={({ field }) => (
                            <TextField
                              {...field}
                              type="date"
                              label="Date"
                              InputLabelProps={{ shrink: true }}
                              sx={{
                                minWidth: 140,
                                '& .MuiOutlinedInput-root': {
                                  borderRadius: 1,
                                  boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                                },
                              }}
                            />
                          )}
                        />

                        {/* Start Time */}
                        <Controller
                          name={`blockDays.${index}.startTime`}
                          control={control}
                          render={({ field }) => (
                            <TextField
                              {...field}
                              type="time"
                              label="From"
                              InputLabelProps={{ shrink: true }}
                              sx={{
                                minWidth: 120,
                                '& .MuiOutlinedInput-root': {
                                  borderRadius: 1,
                                  boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                                },
                              }}
                            />
                          )}
                        />

                        {/* End Time */}
                        <Controller
                          name={`blockDays.${index}.endTime`}
                          control={control}
                          render={({ field }) => (
                            <TextField
                              {...field}
                              type="time"
                              label="Till"
                              InputLabelProps={{ shrink: true }}
                              sx={{
                                minWidth: 120,
                                '& .MuiOutlinedInput-root': {
                                  borderRadius: 1,
                                  boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                                },
                              }}
                            />
                          )}
                        />

                        {/* Delete Button */}
                        <IconButton
                          onClick={() => removeBlock(index)}
                          sx={{
                            backgroundColor: 'white',
                            borderRadius: '10px',
                            boxShadow: '0px 0px 6px 0px rgba(0, 0, 0, 0.16)',
                            '&:hover': {
                              backgroundColor: 'error.50',
                            },
                          }}
                        >
                          <DeleteIcon sx={{ color: '#373E41' }} />
                        </IconButton>
                      </Box>
                    ))}

                    {/* Add Block Day Button */}
                    <Button
                      variant="outlined"
                      startIcon={<AddIcon />}
                      onClick={addBlockDay}
                      sx={{
                        alignSelf: 'flex-start',
                        borderRadius: 1,
                        borderColor: '#233853',
                        color: '#233853',
                        backgroundColor: '#E5F0FF',
                        '&:hover': {
                          backgroundColor: '#D0E0FF',
                          borderColor: '#233853',
                        },
                      }}
                    >
                      Add Block Days
                    </Button>
                  </Box>
                </Box>
              </Box>
            </Box>
          </Box>
        </Box>
      </DialogContent>

      {/* Footer */}
      <DialogActions
        sx={{
          px: 1.5,
          py: 1.5,
          borderTop: '1px solid #E7E7E7',
          gap: 1.5,
        }}
      >
        <Button
          onClick={handleClose}
          variant="outlined"
          sx={{
            borderRadius: 1,
            px: 2,
            py: 0.75,
            textTransform: 'none',
            fontWeight: 500,
            borderColor: '#233853',
            color: '#233853',
          }}
        >
          Close
        </Button>
        <Button
          onClick={handleSubmit(onSubmit)}
          variant="contained"
          disabled={!isValid || isSubmitting}
          sx={{
            borderRadius: 1,
            px: 2,
            py: 0.75,
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
          {isSubmitting ? 'Saving...' : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ProviderAvailabilityModal; 