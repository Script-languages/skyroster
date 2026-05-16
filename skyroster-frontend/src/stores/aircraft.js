import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { initialAircraft, AIRCRAFT_TYPES, BASES, AVAILABILITY_STATUSES } from '../data/mockData'
import apiClient from '../api/axios'

export const useAircraftStore = defineStore('aircraft', () => {
  const aircraft = ref([...initialAircraft])
  const aircraftTypeOptions = AIRCRAFT_TYPES
  const baseOptions = BASES
  const availabilityOptions = AVAILABILITY_STATUSES

  const aircraftCount = computed(() => aircraft.value.length)
  
  const availableAircraft = computed(() => 
    aircraft.value.filter(a => a.dostepnosc === 'dostepny')
  )

  function generateId() {
    const maxId = aircraft.value.reduce((max, a) => {
      const num = parseInt(a.id.replace('A', ''))
      return num > max ? num : max
    }, 0)
    return `A${String(maxId + 1).padStart(3, '0')}`
  }

  function addAircraft(aircraftData) {
    const newAircraft = {
      ...aircraftData,
      id: generateId()
    }
    aircraft.value.push(newAircraft)
    return newAircraft
  }

  function updateAircraft(id, aircraftData) {
    const index = aircraft.value.findIndex(a => a.id === id)
    if (index !== -1) {
      aircraft.value[index] = { ...aircraft.value[index], ...aircraftData }
      return aircraft.value[index]
    }
    return null
  }

  function deleteAircraft(id) {
    const index = aircraft.value.findIndex(a => a.id === id)
    if (index !== -1) {
      aircraft.value.splice(index, 1)
      return true
    }
    return false
  }

  function getAircraftById(id) {
    return aircraft.value.find(a => a.id === id)
  }

  function getAircraftDisplay(id) {
    const a = getAircraftById(id)
    return a ? `${a.rejestracja} (${a.typ})` : ''
  }

  function getAvailabilityInfo(status) {
    return availabilityOptions.find(s => s.value === status)
  }

  async function loadAircraft() {
    try {
      const { data } = await apiClient.get('/aircraft')
      aircraft.value = data.map(a => ({
        id: a.id,
        rejestracja: a.registrationNumber,
        typ: a.aircraftType.icaoCode,
        typId: a.aircraftType.id,
        baza: a.operationalBase.icaoCode,
        bazaId: a.operationalBase.id,
        dostepnosc: 'dostepny'
      }))
      return true
    } catch (e) {
      console.error('loadAircraft failed', e)
      return false
    }
  }

  async function loadAvailableAircraft({ from, to, baseId }) {
    const params = { from, to }
    if (baseId) params.baseId = baseId
    const { data } = await apiClient.get('/aircraft/availability', { params })
    return data.map(a => ({
      id: a.id,
      rejestracja: a.registrationNumber,
      typ: a.aircraftType.icaoCode,
      typId: a.aircraftType.id,
      baza: a.operationalBase.icaoCode
    }))
  }

  return {
    aircraft,
    aircraftCount,
    availableAircraft,
    aircraftTypeOptions,
    baseOptions,
    availabilityOptions,
    addAircraft,
    updateAircraft,
    deleteAircraft,
    getAircraftById,
    getAircraftDisplay,
    getAvailabilityInfo,
    loadAircraft,
    loadAvailableAircraft
  }
})
