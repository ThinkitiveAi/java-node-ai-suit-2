import React, { useState } from 'react'
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton,
  Divider,
  useTheme,
  useMediaQuery,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Stepper,
  Step,
  StepLabel,
} from '@mui/material'
import {
  Visibility,
  VisibilityOff,
  Person,
  Email,
  Phone,
  Lock,
  LocationOn,
  LocalHospital,
  Business,
  School,
} from '@mui/icons-material'
import { useNavigate } from 'react-router-dom'
import { providerApi } from '../../services/providerApi'
import { handleApiError } from '../../utils/errorHandler'

const Register = () => {
  const navigate = useNavigate()
  const theme = useTheme()
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'))
  
  const [activeStep, setActiveStep] = useState(0)
  const [formData, setFormData] = useState({
    // Personal Information
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    
    // Professional Information
    specialization: '',
    licenseNumber: '',
    yearsExperience: '',
    
    // Clinic Address
    streetAddress: '',
    city: '',
    state: '',
    zipCode: '',
    
    // Account Security
    password: '',
    confirmPassword: '',
  })
  
  const [errors, setErrors] = useState({})
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [generalError, setGeneralError] = useState('')
  const [successMessage, setSuccessMessage] = useState('')

  const steps = ['Personal Info', 'Professional Info', 'Clinic Address', 'Security']

  // Validation functions
  const validateField = (name, value) => {
    switch (name) {
      case 'firstName':
      case 'lastName':
        if (!value) return `${name === 'firstName' ? 'First' : 'Last'} name is required`
        if (value.length < 2) return `${name === 'firstName' ? 'First' : 'Last'} name must be at least 2 characters`
        if (value.length > 50) return `${name === 'firstName' ? 'First' : 'Last'} name must be less than 50 characters`
        return ''
      
      case 'email':
        if (!value) return 'Email is required'
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(value)) return 'Please enter a valid email address'
        return ''
      
      case 'phone':
        if (!value) return 'Phone number is required'
        // Updated validation to be more flexible for international phone numbers
        const phoneRegex = /^[\+]?[1-9][\d]{0,20}$/
        if (!phoneRegex.test(value.replace(/[\s\-\(\)]/g, ''))) return 'Please enter a valid phone number'
        return ''
      
      case 'specialization':
        if (!value) return 'Specialization is required'
        if (value.length < 3) return 'Specialization must be at least 3 characters'
        if (value.length > 100) return 'Specialization must be less than 100 characters'
        return ''
      
      case 'licenseNumber':
        if (!value) return 'Medical license number is required'
        if (!/^[A-Za-z0-9\-]+$/.test(value)) return 'License number can only contain letters, numbers, and hyphens'
        return ''
      
      case 'yearsExperience':
        if (!value) return 'Years of experience is required'
        const years = parseInt(value)
        if (isNaN(years) || years < 0 || years > 50) return 'Years of experience must be between 0 and 50'
        return ''
      
      case 'streetAddress':
        if (!value) return 'Street address is required'
        if (value.length > 200) return 'Street address must be less than 200 characters'
        return ''
      
      case 'city':
        if (!value) return 'City is required'
        if (value.length > 100) return 'City must be less than 100 characters'
        return ''
      
      case 'state':
        if (!value) return 'State/Province is required'
        if (value.length > 50) return 'State/Province must be less than 50 characters'
        return ''
      
      case 'zipCode':
        if (!value) return 'ZIP/Postal code is required'
        // Updated validation to be more flexible for different postal code formats
        const zipRegex = /^[A-Za-z0-9\s\-]{3,20}$/
        if (!zipRegex.test(value)) return 'Please enter a valid ZIP/Postal code (3-20 characters)'
        return ''
      
      case 'password':
        if (!value) return 'Password is required'
        if (value.length < 8) return 'Password must be at least 8 characters'
        if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
          return 'Password must contain at least one uppercase letter, one lowercase letter, and one number'
        }
        return ''
      
      case 'confirmPassword':
        if (!value) return 'Please confirm your password'
        if (value !== formData.password) return 'Passwords do not match'
        return ''
      
      default:
        return ''
    }
  }

  const validateStep = (step) => {
    const newErrors = {}
    let isValid = true

    switch (step) {
      case 0: // Personal Information
        ['firstName', 'lastName', 'email', 'phone'].forEach(field => {
          const error = validateField(field, formData[field])
          if (error) {
            newErrors[field] = error
            isValid = false
          }
        })
        break
      
      case 1: // Professional Information
        ['specialization', 'licenseNumber', 'yearsExperience'].forEach(field => {
          const error = validateField(field, formData[field])
          if (error) {
            newErrors[field] = error
            isValid = false
          }
        })
        break
      
      case 2: // Clinic Address
        ['streetAddress', 'city', 'state', 'zipCode'].forEach(field => {
          const error = validateField(field, formData[field])
          if (error) {
            newErrors[field] = error
            isValid = false
          }
        })
        break
      
      case 3: // Security
        ['password', 'confirmPassword'].forEach(field => {
          const error = validateField(field, formData[field])
          if (error) {
            newErrors[field] = error
            isValid = false
          }
        })
        break
    }

    setErrors(newErrors)
    return isValid
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
    
    // Clear field-specific error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
    
    // Clear general error when user makes changes
    if (generalError) {
      setGeneralError('')
    }
  }

  const handleNext = () => {
    if (validateStep(activeStep)) {
      setActiveStep(prev => prev + 1)
    }
  }

  const handleBack = () => {
    setActiveStep(prev => prev - 1)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (!validateStep(activeStep)) {
      return
    }
    
    setIsLoading(true)
    setGeneralError('')
    
    try {
      // Call the API
      const response = await providerApi.register(formData)
      
      // Success
      setSuccessMessage('Registration successful! Redirecting to login...')
      setTimeout(() => {
        navigate('/login')
      }, 2000)
      
    } catch (error) {
      // Handle API errors using utility function
      const errorMessage = handleApiError(error)
      setGeneralError(errorMessage)
    } finally {
      setIsLoading(false)
    }
  }

  const renderStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="First Name"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                error={!!errors.firstName}
                helperText={errors.firstName}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Person color="action" />
                    </InputAdornment>
                  ),
                }}
                disabled={isLoading}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Last Name"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                error={!!errors.lastName}
                helperText={errors.lastName}
                disabled={isLoading}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Email Address"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                error={!!errors.email}
                helperText={errors.email}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Email color="action" />
                    </InputAdornment>
                  ),
                }}
                disabled={isLoading}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Phone Number"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                error={!!errors.phone}
                helperText={errors.phone}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Phone color="action" />
                    </InputAdornment>
                  ),
                }}
                disabled={isLoading}
              />
            </Grid>
          </Grid>
        )
      
      case 1:
        return (
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Specialization"
                name="specialization"
                value={formData.specialization}
                onChange={handleChange}
                error={!!errors.specialization}
                helperText={errors.specialization}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Business color="action" />
                    </InputAdornment>
                  ),
                }}
                disabled={isLoading}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Medical License Number"
                name="licenseNumber"
                value={formData.licenseNumber}
                onChange={handleChange}
                error={!!errors.licenseNumber}
                helperText={errors.licenseNumber}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LocalHospital color="action" />
                    </InputAdornment>
                  ),
                }}
                disabled={isLoading}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Years of Experience"
                name="yearsExperience"
                type="number"
                value={formData.yearsExperience}
                onChange={handleChange}
                error={!!errors.yearsExperience}
                helperText={errors.yearsExperience}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <School color="action" />
                    </InputAdornment>
                  ),
                }}
                disabled={isLoading}
              />
            </Grid>
          </Grid>
        )
      
      case 2:
        return (
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Street Address"
                name="streetAddress"
                value={formData.streetAddress}
                onChange={handleChange}
                error={!!errors.streetAddress}
                helperText={errors.streetAddress}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LocationOn color="action" />
                    </InputAdornment>
                  ),
                }}
                disabled={isLoading}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="City"
                name="city"
                value={formData.city}
                onChange={handleChange}
                error={!!errors.city}
                helperText={errors.city}
                disabled={isLoading}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="State/Province"
                name="state"
                value={formData.state}
                onChange={handleChange}
                error={!!errors.state}
                helperText={errors.state}
                disabled={isLoading}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="ZIP/Postal Code"
                name="zipCode"
                value={formData.zipCode}
                onChange={handleChange}
                error={!!errors.zipCode}
                helperText={errors.zipCode}
                disabled={isLoading}
              />
            </Grid>
          </Grid>
        )
      
      case 3:
        return (
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Password"
                name="password"
                type={showPassword ? 'text' : 'password'}
                value={formData.password}
                onChange={handleChange}
                error={!!errors.password}
                helperText={errors.password}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock color="action" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowPassword(!showPassword)}
                        edge="end"
                        disabled={isLoading}
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
                disabled={isLoading}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Confirm Password"
                name="confirmPassword"
                type={showConfirmPassword ? 'text' : 'password'}
                value={formData.confirmPassword}
                onChange={handleChange}
                error={!!errors.confirmPassword}
                helperText={errors.confirmPassword}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock color="action" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                        edge="end"
                        disabled={isLoading}
                      >
                        {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
                disabled={isLoading}
              />
            </Grid>
          </Grid>
        )
      
      default:
        return null
    }
  }

  return (
    <Container component="main" maxWidth="md">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          py: 4,
        }}
      >
        <Paper 
          elevation={isMobile ? 0 : 8} 
          sx={{ 
            p: isMobile ? 2 : 4, 
            width: '100%',
            borderRadius: isMobile ? 0 : 2,
            background: isMobile ? 'transparent' : 'background.paper',
          }}
        >
          {/* Header */}
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
              <LocalHospital 
                sx={{ 
                  fontSize: 48, 
                  color: 'primary.main',
                  mb: 1 
                }} 
              />
            </Box>
            <Typography 
              component="h1" 
              variant={isMobile ? "h5" : "h4"} 
              gutterBottom
              sx={{ fontWeight: 600 }}
            >
              Provider Registration
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Create your healthcare provider account
            </Typography>
          </Box>

          {/* Stepper */}
          <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          {/* Success/Error Messages */}
          {successMessage && (
            <Alert severity="success" sx={{ mb: 3 }}>
              {successMessage}
            </Alert>
          )}
          
          {generalError && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {generalError}
            </Alert>
          )}

          {/* Form Content */}
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            {renderStepContent(activeStep)}

            {/* Navigation Buttons */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
              <Button
                disabled={activeStep === 0 || isLoading}
                onClick={handleBack}
                variant="outlined"
              >
                Back
              </Button>
              
              <Box>
                {activeStep === steps.length - 1 ? (
                  <Button
                    type="submit"
                    variant="contained"
                    disabled={isLoading}
                    sx={{ 
                      py: 1.5,
                      px: 4,
                      fontSize: '1.1rem',
                      fontWeight: 600,
                    }}
                  >
                    {isLoading ? (
                      <CircularProgress size={24} color="inherit" />
                    ) : (
                      'Create Account'
                    )}
                  </Button>
                ) : (
                  <Button
                    variant="contained"
                    onClick={handleNext}
                    disabled={isLoading}
                    sx={{ 
                      py: 1.5,
                      px: 4,
                      fontSize: '1.1rem',
                      fontWeight: 600,
                    }}
                  >
                    Next
                  </Button>
                )}
              </Box>
            </Box>
          </Box>

          <Divider sx={{ my: 3 }}>
            <Typography variant="body2" color="text.secondary">
              Already have an account?
            </Typography>
          </Divider>

          <Button
            fullWidth
            variant="outlined"
            onClick={() => navigate('/login')}
            disabled={isLoading}
            sx={{ 
              py: 1.5,
              fontSize: '1.1rem',
              fontWeight: 600,
            }}
          >
            Sign In to Existing Account
          </Button>
        </Paper>

        {/* Footer */}
        <Box sx={{ mt: 4, textAlign: 'center' }}>
          <Typography variant="body2" color="text.secondary">
            © 2024 Health First. All rights reserved.
          </Typography>
        </Box>
      </Box>
    </Container>
  )
}

export default Register 