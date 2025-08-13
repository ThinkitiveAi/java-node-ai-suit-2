import api from './api'
import API_CONFIG from '../config/apiConfig'

// Provider Availability API Service
export const availabilityApi = {
  // Get Provider Availability
  getAvailability: async () => {
    try {
      const response = await api.get(API_CONFIG.ENDPOINTS.PROVIDER.AVAILABILITY)
      return response.data
    } catch (error) {
      console.error('Get Availability - Error:', error)
      throw error
    }
  },

  // Create/Update Provider Availability
  saveAvailability: async (availabilityData) => {
    try {
      console.log('Save Availability - Request Data:', availabilityData)
      
      const response = await api.post(API_CONFIG.ENDPOINTS.PROVIDER.AVAILABILITY, availabilityData)
      
      console.log('Save Availability - Response:', response)
      
      return response.data
    } catch (error) {
      console.error('Save Availability - Error:', error)
      console.error('Error Response:', error.response)
      throw error
    }
  },

  // Delete Provider Availability
  deleteAvailability: async (availabilityId) => {
    try {
      const response = await api.delete(`${API_CONFIG.ENDPOINTS.PROVIDER.AVAILABILITY}/${availabilityId}`)
      return response.data
    } catch (error) {
      console.error('Delete Availability - Error:', error)
      throw error
    }
  },

  // Update specific availability entry
  updateAvailability: async (availabilityId, availabilityData) => {
    try {
      const response = await api.put(`${API_CONFIG.ENDPOINTS.PROVIDER.AVAILABILITY}/${availabilityId}`, availabilityData)
      return response.data
    } catch (error) {
      console.error('Update Availability - Error:', error)
      throw error
    }
  }
}

export default availabilityApi 