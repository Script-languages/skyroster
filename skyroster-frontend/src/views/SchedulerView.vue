<script setup>
import { onMounted, ref } from 'vue'
import { useFlightsStore } from '../stores/flights'
import { useAircraftStore } from '../stores/aircraft'
import { usePilotsStore } from '../stores/pilots'
import { useToast } from 'primevue/usetoast'
import Button from 'primevue/button'
import Toast from 'primevue/toast'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import DatePicker from 'primevue/datepicker'
import FlightScheduler from '../components/scheduler/FlightScheduler.vue'
import FlightWizardDialog from '../components/scheduler/FlightWizardDialog.vue'
import { BASES } from '../data/mockData' // Add BASES import

const flightsStore = useFlightsStore()
const aircraftStore = useAircraftStore()
const pilotsStore = usePilotsStore()
const toast = useToast()

const schedulerRef = ref(null)
const dialogVisible = ref(false)
const wizardVisible = ref(false)
const isEditMode = ref(false)
const selectedFlight = ref(null)

const flightForm = ref(getEmptyForm())

onMounted(async () => {
  await Promise.all([
    aircraftStore.loadAircraft(),
    aircraftStore.fetchAircraftList(),
    pilotsStore.loadPilots(),
    flightsStore.loadFlights(),
    flightsStore.loadPlanningSchedules()
  ])

async function onFlightCreated() {
  toast.add({ severity: 'success', summary: 'Sukces', detail: 'Lot został utworzony', life: 3000 })
  await flightsStore.loadFlights()
}

const baseOptions = BASES.map(b => ({
  label: `${b.value} - ${b.label}`,
  value: b.value
}))

const pilotOptions = pilotsStore.pilots.map(p => ({
  label: `${p.imie} ${p.nazwisko}`,
  value: p.id
}))

const aircraftOptions = aircraftStore.aircraft.map(a => ({
  label: `${a.rejestracja} (${a.typ})`,
  value: a.id
}))

function getEmptyForm() {
  const today = new Date()
  return {
    aircraftId: null,
    pilotId: null,
    startDate: today,
    startTime: new Date(1970, 0, 1, 8, 0),
    endDate: today,
    endTime: new Date(1970, 0, 1, 10, 0),
    departure: null,
    arrival: null,
    description: ''
  }
}

function formatDateTimeForStore(date, time) {
  if (!date || !time) return null
  const d = new Date(date)
  const t = new Date(time)
  d.setHours(t.getHours(), t.getMinutes(), 0, 0)
  return d.toISOString().slice(0, 19)
}

function parseDateTimeFromStore(dateTimeStr) {
  if (!dateTimeStr) return { date: null, time: null }
  const d = new Date(dateTimeStr)
  return {
    date: new Date(d.getFullYear(), d.getMonth(), d.getDate()),
    time: new Date(1970, 0, 1, d.getHours(), d.getMinutes())
  }
}

function handleEventClick(flight) {
  selectedFlight.value = flight
  isEditMode.value = true

  const startParsed = parseDateTimeFromStore(flight.start)
  const endParsed = parseDateTimeFromStore(flight.end)

  // Extract departure and arrival from text if possible
  let dep = null
  let arr = null
  if (flight.text) {
    const parts = flight.text.split(' \u2192 ')
    if (parts.length === 2) {
      dep = parts[0]
      arr = parts[1]
    } else {
      const partsArrow = flight.text.split(' -> ')
      if (partsArrow.length === 2) {
        dep = partsArrow[0]
        arr = partsArrow[1]
      }
    }
  }

  flightForm.value = {
    aircraftId: flight.aircraftId,
    pilotId: flight.pilotId,
    startDate: startParsed.date,
    startTime: startParsed.time,
    endDate: endParsed.date,
    endTime: endParsed.time,
    departure: dep,
    arrival: arr,
    description: flight.description || ''
  }

  dialogVisible.value = true
}

function handleTimeRangeSelected(selection) {
  selectedFlight.value = null
  isEditMode.value = false

  const startValue = selection.start.value ? selection.start.value : selection.start.toString()
  const endValue = selection.end.value ? selection.end.value : selection.end.toString()

  const startDate = new Date(startValue)
  new Date(endValue)

  flightForm.value = {
    ...getEmptyForm(),
    aircraftId: selection.aircraftId,
    startDate: startDate,
    startTime: new Date(1970, 0, 1, 8, 0),
    endDate: startDate,
    endTime: new Date(1970, 0, 1, 10, 0)
  }

  dialogVisible.value = true
}

function openAddDialog() {
  wizardVisible.value = true
}

function saveFlight() {
  if (!flightForm.value.startDate || !flightForm.value.startTime || !flightForm.value.endDate || !flightForm.value.endTime) {
    toast.add({ severity: 'error', summary: 'Błąd', detail: 'Podaj poprawne daty i godziny lotu', life: 3000 })
    return
  }

  const flightData = {
    aircraftId: flightForm.value.aircraftId,
    pilotId: flightForm.value.pilotId,
    start: formatDateTimeForStore(flightForm.value.startDate, flightForm.value.startTime),
    end: formatDateTimeForStore(flightForm.value.endDate, flightForm.value.endTime),
    text: `${flightForm.value.departure} \u2192 ${flightForm.value.arrival}`,
    description: flightForm.value.description
  }

  if (isEditMode.value && selectedFlight.value) {
    flightsStore.updateFlight(selectedFlight.value.id, flightData)
    toast.add({ severity: 'success', summary: 'Sukces', detail: 'Lot został zaktualizowany', life: 3000 })
  } else {
    flightsStore.addFlight(flightData)
    toast.add({ severity: 'success', summary: 'Sukces', detail: 'Lot został dodany', life: 3000 })
  }

  dialogVisible.value = false
}

function deleteFlight() {
  if (selectedFlight.value) {
    flightsStore.deleteFlight(selectedFlight.value.id)
    toast.add({ severity: 'success', summary: 'Sukces', detail: 'Lot został usunięty', life: 3000 })
    dialogVisible.value = false
  }
}

function navigateToday() {
  schedulerRef.value?.navigateToToday()
}

function navigatePrev() {
  schedulerRef.value?.navigatePrevious()
}

function navigateNext() {
  schedulerRef.value?.navigateNext()
}
</script>

<template>
  <div class="scheduler-view">
    <Toast />

    <div class="page-header">
      <h1>Harmonogram lotów</h1>
      <div class="header-actions">
        <div class="navigation-buttons">
          <Button icon="pi pi-chevron-left" severity="secondary" text @click="navigatePrev" />
          <Button label="Dzisiaj" severity="secondary" text @click="navigateToday" />
          <Button icon="pi pi-chevron-right" severity="secondary" text @click="navigateNext" />
        </div>
        <Button label="Dodaj lot" icon="pi pi-plus" @click="openAddDialog" />
      </div>
    </div>

    <div class="scheduler-container">
      <FlightScheduler
        ref="schedulerRef"
        @eventClick="handleEventClick"
        @timeRangeSelected="handleTimeRangeSelected"
      />
    </div>

    <Dialog
      v-model:visible="dialogVisible"
      :header="isEditMode ? 'Edytuj lot' : 'Dodaj lot'"
      :style="{ width: '600px' }"
      :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
      modal
    >
      <div class="form-grid">
        <div class="form-field">
          <label for="aircraft">Samolot</label>
          <Select
            id="aircraft"
            v-model="flightForm.aircraftId"
            :options="aircraftOptions"
            optionLabel="label"
            optionValue="value"
            placeholder="Wybierz samolot"
            class="w-full"
          />
        </div>

        <div class="form-field">
          <label for="pilot">Pilot</label>
          <Select
            id="pilot"
            v-model="flightForm.pilotId"
            :options="pilotOptions"
            optionLabel="label"
            optionValue="value"
            placeholder="Wybierz pilota"
            class="w-full"
          />
        </div>

        <div class="form-field">
          <label for="startDate">Data rozpoczęcia</label>
          <DatePicker
            id="startDate"
            v-model="flightForm.startDate"
            dateFormat="dd.mm.yy"
            class="w-full"
          />
        </div>

        <div class="form-field">
          <label for="startTime">Godzina rozpoczęcia</label>
          <DatePicker
            id="startTime"
            v-model="flightForm.startTime"
            timeOnly
            hourFormat="24"
            class="w-full"
          />
        </div>

        <div class="form-field">
          <label for="endDate">Data zakończenia</label>
          <DatePicker
            id="endDate"
            v-model="flightForm.endDate"
            dateFormat="dd.mm.yy"
            class="w-full"
          />
        </div>

        <div class="form-field">
          <label for="endTime">Godzina zakończenia</label>
          <DatePicker
            id="endTime"
            v-model="flightForm.endTime"
            timeOnly
            hourFormat="24"
            class="w-full"
          />
        </div>

        <div class="form-field">
          <label for="departure">Lotnisko startowe</label>
          <Select
            id="departure"
            v-model="flightForm.departure"
            :options="baseOptions"
            optionLabel="label"
            optionValue="value"
            placeholder="Wybierz startowe"
            class="w-full"
          />
        </div>

        <div class="form-field">
          <label for="arrival">Lotnisko docelowe</label>
          <Select
            id="arrival"
            v-model="flightForm.arrival"
            :options="baseOptions"
            optionLabel="label"
            optionValue="value"
            placeholder="Wybierz docelowe"
            class="w-full"
          />
        </div>

        <div class="form-field full-width">
          <label for="description">Opis</label>
          <InputText id="description" v-model="flightForm.description" class="w-full" />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <Button v-if="isEditMode" label="Usuń" severity="danger" text @click="deleteFlight" />
          <div class="spacer"></div>
          <Button label="Anuluj" severity="secondary" @click="dialogVisible = false" />
          <Button label="Zapisz" @click="saveFlight" />
        </div>
      </template>
    </Dialog>

    <FlightWizardDialog v-model:visible="wizardVisible" @created="onFlightCreated" />
  </div>
</template>

<style scoped>
.scheduler-view {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 3rem);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.page-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #1e293b;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.navigation-buttons {
  display: flex;
  gap: 0.25rem;
  align-items: center;
}

.scheduler-container {
  flex: 1;
  min-height: 0;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-field.full-width {
  grid-column: 1 / -1;
}

.form-field label {
  font-weight: 600;
  color: #3b82f6;
  font-size: 0.95rem;
}

.w-full {
  width: 100%;
}

.dialog-footer {
  display: flex;
  gap: 0.5rem;
  width: 100%;
}

.spacer {
  flex: 1;
}
</style>
