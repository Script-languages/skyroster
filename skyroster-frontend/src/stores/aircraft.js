import {computed, ref} from 'vue'
import {defineStore} from 'pinia'
import apiClient from '../api/axios'
import {AIRCRAFT_TYPES, AVAILABILITY_STATUSES, BASES} from '../data/mockData'

async function getAircraftList() {
  const res = await apiClient.get('/aircraft', {
    headers: {
      'Content-Type': 'application/json'
    }
  });

  return res.data;
}

export const useAircraftStore = defineStore('aircraft', () => {
  const aircraft = ref([]);
  const aircraftTypeOptions = AIRCRAFT_TYPES
  const baseOptions = BASES
  const availabilityOptions = AVAILABILITY_STATUSES

  const aircraftCount = computed(() => aircraft.value.length)

  const availableAircraft = computed(() =>
    aircraft.value.filter(a => a.dostepnosc === 'dostepny')
  )

  async function fetchAircraftList() {
    try {
      const response = await getAircraftList();

      aircraft.value = response.map(aircraft => ({
        id: aircraft.id,
        rejestracja: aircraft.registrationNumber,
        typ: aircraft.aircraftType.icaoCode,
        baza: aircraft.operationalBase.icaoCode
      }));
    } catch (error) {
      console.error(error);
    }
  }

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

  async function deleteAircraft(id) {
    try {
      const res = await apiClient.delete(
        `/aircraft/${id}`,
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );

      if (res.status >= 200 && res.status < 300) {
        const index = aircraft.value.findIndex(a => a.id === id);

        if (index !== -1) {
          aircraft.value.splice(index, 1);
        }

        return {
          success: true
        };
      }
    } catch (error) {
      console.error(error);

      return {
        success: false,
        message:
          error.response?.data?.message ||
          'Failed to delete aircraft'
      };
    }

    return {
      success: false,
      message: 'Unexpected error occurred'
    };
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
    fetchAircraftList
  }
})
