import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { initialFlights } from '../data/mockData'
import { useAircraftStore } from './aircraft'
import { usePilotsStore } from './pilots'

export const useFlightsStore = defineStore('flights', () => {
  const flights = ref([...initialFlights])

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

  function getSchedulerEvents() {
    const aircraftStore = useAircraftStore()
    const pilotsStore = usePilotsStore()
    
    return flights.value.map(flight => ({
      id: flight.id,
      resource: flight.aircraftId,
      start: flight.start,
      end: flight.end,
      text: flight.text,
      barColor: '#3B82F6',
      pilotName: pilotsStore.getPilotFullName(flight.pilotId),
      aircraftDisplay: aircraftStore.getAircraftDisplay(flight.aircraftId)
    }))
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
    getFlightById,
    getSchedulerEvents,
    getSchedulerResources
  }
})
