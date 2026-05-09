import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { initialFlights } from '../data/mockData'
import apiClient from '../api/axios'
import { useAircraftStore } from './aircraft'
import { usePilotsStore } from './pilots'

export const useFlightsStore = defineStore('flights', () => {
  const flights = ref([...initialFlights])
  const planningSchedules = ref([])

  const flightsCount = computed(() => flights.value.length)

  function generateId() {
    const maxId = flights.value.reduce((max, flight) => {
      const num = parseInt(flight.id.replace('F', ''))
      return num > max ? num : max
    }, 0)
    return `F${String(maxId + 1).padStart(3, '0')}`
  }

  function addFlight(flightData) {
    const newFlight = {
      ...flightData,
      id: generateId()
    }
    flights.value.push(newFlight)
    return newFlight
  }

  function updateFlight(id, flightData) {
    const index = flights.value.findIndex(f => f.id === id)
    if (index !== -1) {
      flights.value[index] = { ...flights.value[index], ...flightData }
      return flights.value[index]
    }
    return null
  }

  function deleteFlight(id) {
    const index = flights.value.findIndex(f => f.id === id)
    if (index !== -1) {
      flights.value.splice(index, 1)
      return true
    }
    return false
  }

  function getFlightById(id) {
    return flights.value.find(f => f.id === id)
  }

  function parseScheduleDate(value) {
    if (!value || typeof value !== 'string') return null
    const datePart = value.includes('T') ? value.split('T')[0] : value
    const [year, month, day] = datePart.split('-').map(Number)
    if (!year || !month || !day) return null
    return new Date(year, month - 1, day)
  }

  function formatDatePart(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  function getPlanningScheduleEvents(aircraftStore, pilotsStore) {
    const aircraftIds = aircraftStore.aircraft.map(aircraft => aircraft.id)
    if (!aircraftIds.length) return []

    return planningSchedules.value
      .map((schedule, index) => {
        const startDate = parseScheduleDate(schedule.date)
        if (!startDate) return null

        const endDate = new Date(startDate)
        endDate.setDate(endDate.getDate() + 1)

        const pilot = pilotsStore.pilots.find(p => `${p.imie} ${p.nazwisko}` === schedule.pilotName)
        const eventId = schedule.id ? `API-${schedule.id}` : `API-${index + 1}`

        return {
          id: eventId,
          resource: aircraftIds[index % aircraftIds.length],
          start: `${formatDatePart(startDate)}T00:00:00`,
          end: `${formatDatePart(endDate)}T00:00:00`,
          text: `${schedule.pilotName} (API)`,
          barColor: '#0EA5E9',
          pilotName: schedule.pilotName,
          pilotId: pilot?.id ?? null,
          isApiSchedule: true
        }
      })
      .filter(Boolean)
  }

  async function loadPlanningSchedules() {
    try {
      const response = await apiClient.get('/planning/schedules', {
        headers: {
          Accept: 'application/json'
        }
      })

      if (!Array.isArray(response.data)) {
        throw new Error('Planning schedules response is not an array')
      }

      planningSchedules.value = response.data
      return true
    } catch (error) {
      planningSchedules.value = []
      console.error('Nie udało się pobrać harmonogramu z API. Pozostają lokalne mocki.', error)
      return false
    }
  }

  function getSchedulerEvents() {
    const aircraftStore = useAircraftStore()
    const pilotsStore = usePilotsStore()

    const localEvents = flights.value.map(flight => ({
      id: flight.id,
      resource: flight.aircraftId,
      start: flight.start,
      end: flight.end,
      text: flight.text,
      barColor: '#3B82F6',
      pilotName: pilotsStore.getPilotFullName(flight.pilotId),
      aircraftDisplay: aircraftStore.getAircraftDisplay(flight.aircraftId)
    }))

    return [...localEvents, ...getPlanningScheduleEvents(aircraftStore, pilotsStore)]
  }

  function getSchedulerResources() {
    const aircraftStore = useAircraftStore()
    
    return aircraftStore.aircraft.map(aircraft => ({
      id: aircraft.id,
      name: `${aircraft.rejestracja} (${aircraft.typ})`
    }))
  }

  return {
    flights,
    flightsCount,
    addFlight,
    updateFlight,
    deleteFlight,
    loadPlanningSchedules,
    getFlightById,
    getSchedulerEvents,
    getSchedulerResources
  }
})
