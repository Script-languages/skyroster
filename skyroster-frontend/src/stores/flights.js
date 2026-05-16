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

  async function addFlight(flightData) {
    try {
      const { data } = await apiClient.post('/flights', flightData)
      const mapped = {
        id: data.id,
        aircraftId: data.aircraft?.id,
        pilotId: data.pilot?.id ?? null,
        start: data.flightStart,
        end: data.flightEnd,
        text: data.startAirport && data.endAirport
          ? `${data.startAirport.icaoCode} → ${data.endAirport.icaoCode}`
          : '',
        description: data.description
      }
      flights.value.push(mapped)
      return { ok: true, flight: mapped }
    } catch (e) {
      if (e.response?.status === 422) {
        return { ok: false, status: 422, violations: e.response.data.violations ?? [] }
      }
      if (e.response?.status === 409) {
        return { ok: false, status: 409, message: e.response.data?.message ?? 'Konflikt zasobu' }
      }
      return { ok: false, status: e.response?.status ?? 500, message: e.response?.data?.message ?? e.message }
    }
  }

  async function loadFlights() {
    try {
      const { data } = await apiClient.get('/flights')
      flights.value = data.map(f => ({
        id: f.id,
        aircraftId: f.aircraft?.id,
        pilotId: f.pilot?.id ?? null,
        start: f.flightStart,
        end: f.flightEnd,
        text: f.startAirport && f.endAirport
          ? `${f.startAirport.icaoCode} → ${f.endAirport.icaoCode}`
          : '',
        description: f.description
      }))
      return true
    } catch (e) {
      console.error('loadFlights failed', e)
      return false
    }
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
    const localFlight = flights.value.find(f => f.id === id)
    if (localFlight) return localFlight

    if (id.startsWith('API-')) {
      const apiId = id.replace('API-', '')
      const schedule = planningSchedules.value.find((s, index) =>
        String(s.id) === apiId || String(index + 1) === apiId
      )

      if (schedule) {
        const start = parseScheduleDateTime(schedule.startDateTime ?? schedule.date)
        return {
          id: id,
          pilotName: schedule.pilotName,
          start: start ? formatDateTimeForEvent(start) : null,
          isApi: true
        }
      }
    }
    return null
  }

  function parseScheduleDateTime(value) {
    if (!value || typeof value !== 'string') return null
    const date = new Date(value)
    return Number.isNaN(date.getTime()) ? null : date
  }

  function formatDateTimeForEvent(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day}T${hours}:${minutes}:00`
  }

  function formatTimeLabel(date) {
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${hours}:${minutes}`
  }

  function getPlanningScheduleEvents(aircraftStore, pilotsStore) {
    const aircraftIds = aircraftStore.aircraft.map(aircraft => aircraft.id)
    if (!aircraftIds.length) return []

    return planningSchedules.value
      .map((schedule, index) => {
        const startDateTime = parseScheduleDateTime(schedule.startDateTime ?? schedule.date)
        if (!startDateTime) return null

        let endDateTime = parseScheduleDateTime(schedule.endDateTime)
        if (!endDateTime) {
          endDateTime = new Date(startDateTime.getTime() + 2 * 60 * 60 * 1000)
        }

        const pilot = pilotsStore.pilots.find(p => `${p.imie} ${p.nazwisko}` === schedule.pilotName)
        const eventId = schedule.id ? `API-${schedule.id}` : `API-${index + 1}`
        const routeInfo = schedule.route || `${schedule.pilotName || 'Planowany lot'}`
        const displayText = `${routeInfo} \u2192 ${formatTimeLabel(endDateTime)}`

        return {
          id: eventId,
          resource: aircraftIds[index % aircraftIds.length],
          start: formatDateTimeForEvent(startDateTime),
          end: formatDateTimeForEvent(endDateTime),
          text: displayText,
          barColor: '#3B82F6',
          pilotName: schedule.pilotName,
          pilotId: pilot?.id ?? null,
          isApiSchedule: true,
          aircraftDisplay: aircraftStore.getAircraftDisplay(aircraftIds[index % aircraftIds.length])
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

    const localEvents = flights.value.map(flight => {
      const endDate = new Date(flight.end)
      const formattedEndTime = !Number.isNaN(endDate.getTime())
        ? formatTimeLabel(endDate)
        : ''

      const displayText = `${flight.text} \u2192 ${formattedEndTime}`

      return {
        id: flight.id,
        resource: flight.aircraftId,
        start: flight.start,
        end: flight.end,
        text: displayText,
        barColor: '#3B82F6',
        pilotName: pilotsStore.getPilotFullName(flight.pilotId),
        aircraftDisplay: aircraftStore.getAircraftDisplay(flight.aircraftId)
      }
    })

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
    loadFlights,
    getFlightById,
    getSchedulerEvents,
    getSchedulerResources
  }
})
