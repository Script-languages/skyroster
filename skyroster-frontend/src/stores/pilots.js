import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { initialPilots, QUALIFICATIONS, AIRCRAFT_TYPES, BASES } from '../data/mockData'
import apiClient from '../api/axios'

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

  async function loadPilots() {
    try {
      const { data } = await apiClient.get('/pilots', { params: { page: 0, size: 100 } })
      const items = data.content ?? data ?? []
      pilots.value = items.map(mapApiPilot)
      return true
    } catch (e) {
      console.error('loadPilots failed', e)
      return false
    }
  }

  async function loadAvailablePilots({ from, to, aircraftTypeId }) {
    const params = { from, to }
    if (aircraftTypeId) params.aircraftTypeId = aircraftTypeId
    const { data } = await apiClient.get('/pilots/availability', { params })
    return data.map(mapApiPilot)
  }

  function mapApiPilot(p) {
    return {
      id: p.id,
      imie: p.firstName ?? p.name ?? p.imie,
      nazwisko: p.surname ?? p.nazwisko,
      licencja: p.licence ?? p.licencja,
      kwalifikacje: (p.qualifications ?? p.kwalifikacje ?? []).map(q => q.value ?? q),
      typySamolotow: (p.aircraftTypes ?? p.typySamolotow ?? []).map(t => t.icaoCode ?? t),
      bazaMacierzysta: p.homeBase?.icaoCode ?? p.operationalBase?.icaoCode ?? p.bazaMacierzysta ?? null
    }
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
    getPilotFullName,
    loadPilots,
    loadAvailablePilots
  }
})
