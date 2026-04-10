import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { initialPilots, QUALIFICATIONS, AIRCRAFT_TYPES, BASES } from '../data/mockData'

export const usePilotsStore = defineStore('pilots', () => {
  const pilots = ref([...initialPilots])
  const qualificationOptions = QUALIFICATIONS
  const aircraftTypeOptions = AIRCRAFT_TYPES
  const baseOptions = BASES

  const pilotsCount = computed(() => pilots.value.length)

  function generateId() {
    const maxId = pilots.value.reduce((max, pilot) => {
      const num = parseInt(pilot.id.replace('P', ''))
      return num > max ? num : max
    }, 0)
    return `P${String(maxId + 1).padStart(3, '0')}`
  }

  function addPilot(pilotData) {
    const newPilot = {
      ...pilotData,
      id: generateId()
    }
    pilots.value.push(newPilot)
    return newPilot
  }

  function updatePilot(id, pilotData) {
    const index = pilots.value.findIndex(p => p.id === id)
    if (index !== -1) {
      pilots.value[index] = { ...pilots.value[index], ...pilotData }
      return pilots.value[index]
    }
    return null
  }

  function deletePilot(id) {
    const index = pilots.value.findIndex(p => p.id === id)
    if (index !== -1) {
      pilots.value.splice(index, 1)
      return true
    }
    return false
  }

  function getPilotById(id) {
    return pilots.value.find(p => p.id === id)
  }

  function getPilotFullName(id) {
    const pilot = getPilotById(id)
    return pilot ? `${pilot.imie} ${pilot.nazwisko}` : ''
  }

  return {
    pilots,
    pilotsCount,
    qualificationOptions,
    aircraftTypeOptions,
    baseOptions,
    addPilot,
    updatePilot,
    deletePilot,
    getPilotById,
    getPilotFullName
  }
})
