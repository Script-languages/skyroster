<script setup>
import { computed, ref, watch } from 'vue'
import Dialog from 'primevue/dialog'
import Stepper from 'primevue/stepper'
import StepList from 'primevue/steplist'
import Step from 'primevue/step'
import StepPanels from 'primevue/steppanels'
import StepPanel from 'primevue/steppanel'
import Select from 'primevue/select'
import DatePicker from 'primevue/datepicker'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import Message from 'primevue/message'
import { useAircraftStore } from '../../stores/aircraft'
import { usePilotsStore } from '../../stores/pilots'
import { useFlightsStore } from '../../stores/flights'
import { BASES } from '../../data/mockData'

const props = defineProps({
  visible: { type: Boolean, default: false }
})
const emit = defineEmits(['update:visible', 'created'])

const aircraftStore = useAircraftStore()
const pilotsStore = usePilotsStore()
const flightsStore = useFlightsStore()

const visibleProxy = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v)
})

const activeStep = ref('1')

const form = ref(emptyForm())
const availableAircraft = ref([])
const availablePilots = ref([])
const loadingAircraft = ref(false)
const loadingPilots = ref(false)
const submitting = ref(false)
const violations = ref([])
const submitError = ref(null)

const baseOptions = BASES.map(b => ({ label: b.label, value: b.id, icaoCode: b.icaoCode }))

function emptyForm() {
  const today = new Date()
  return {
    departureBaseId: null,
    startDate: today,
    startTime: new Date(1970, 0, 1, 8, 0),
    endDate: today,
    endTime: new Date(1970, 0, 1, 10, 0),
    aircraftId: null,
    pilotId: null,
    arrivalBaseId: null,
    description: ''
  }
}

watch(() => props.visible, v => {
  if (v) {
    form.value = emptyForm()
    activeStep.value = '1'
    availableAircraft.value = []
    availablePilots.value = []
    violations.value = []
    submitError.value = null
  }
})

function combineDateTime(date, time) {
  const d = new Date(date)
  const t = new Date(time)
  d.setHours(t.getHours(), t.getMinutes(), 0, 0)
  return d.toISOString()
}

function fromIso() { return combineDateTime(form.value.startDate, form.value.startTime) }
function toIso() { return combineDateTime(form.value.endDate, form.value.endTime) }

async function loadAircraftStep() {
  loadingAircraft.value = true
  try {
    availableAircraft.value = await aircraftStore.loadAvailableAircraft({
      from: fromIso(),
      to: toIso(),
      baseId: form.value.departureBaseId
    })
  } finally {
    loadingAircraft.value = false
  }
}

async function loadPilotsStep() {
  loadingPilots.value = true
  try {
    const selectedAircraft = availableAircraft.value.find(a => a.id === form.value.aircraftId)
    availablePilots.value = await pilotsStore.loadAvailablePilots({
      from: fromIso(),
      to: toIso(),
      aircraftTypeId: selectedAircraft?.typId
    })
  } finally {
    loadingPilots.value = false
  }
}

async function submit() {
  submitting.value = true
  violations.value = []
  submitError.value = null
  const result = await flightsStore.addFlight({
    aircraftId: form.value.aircraftId,
    pilotId: form.value.pilotId,
    startDateTime: fromIso(),
    endDateTime: toIso(),
    startAirportId: form.value.departureBaseId,
    endAirportId: form.value.arrivalBaseId,
    description: form.value.description || null
  })
  submitting.value = false
  if (result.ok) {
    emit('created', result.flight)
    visibleProxy.value = false
  } else if (result.status === 422) {
    violations.value = result.violations
  } else {
    submitError.value = result.message ?? 'Błąd zapisu lotu'
  }
}

const step2Valid = computed(() => {
  if (!form.value.startDate || !form.value.endDate) return false
  const f = new Date(fromIso())
  const t = new Date(toIso())
  return f.getTime() < t.getTime()
})

const aircraftOptions = computed(() => availableAircraft.value.map(a => ({
  label: `${a.rejestracja} (${a.typ})`, value: a.id
})))
const pilotOptions = computed(() => availablePilots.value.map(p => ({
  label: `${p.imie} ${p.nazwisko}`, value: p.id
})))
</script>

<template>
  <Dialog v-model:visible="visibleProxy" header="Dodaj lot" :style="{ width: '720px' }" modal>
    <Stepper v-model:value="activeStep" linear>
      <StepList>
        <Step value="1">Lotnisko startu</Step>
        <Step value="2">Okno czasowe</Step>
        <Step value="3">Samolot</Step>
        <Step value="4">Pilot</Step>
        <Step value="5">Cel + opis</Step>
      </StepList>

      <StepPanels>
        <StepPanel v-slot="{ activateCallback }" value="1">
          <div class="step-body">
            <label for="departure">Lotnisko startowe</label>
            <Select id="departure" v-model="form.departureBaseId" :options="baseOptions"
                    optionLabel="label" optionValue="value" placeholder="Wybierz" class="w-full" />
          </div>
          <div class="step-footer">
            <Button label="Dalej" :disabled="!form.departureBaseId" @click="activateCallback('2')" />
          </div>
        </StepPanel>

        <StepPanel v-slot="{ activateCallback }" value="2">
          <div class="step-body grid">
            <div>
              <label>Data start</label>
              <DatePicker v-model="form.startDate" dateFormat="dd.mm.yy" class="w-full" />
            </div>
            <div>
              <label>Godzina start</label>
              <DatePicker v-model="form.startTime" timeOnly hourFormat="24" class="w-full" />
            </div>
            <div>
              <label>Data koniec</label>
              <DatePicker v-model="form.endDate" dateFormat="dd.mm.yy" class="w-full" />
            </div>
            <div>
              <label>Godzina koniec</label>
              <DatePicker v-model="form.endTime" timeOnly hourFormat="24" class="w-full" />
            </div>
          </div>
          <Message v-if="!step2Valid" severity="warn" :closable="false">
            Godzina rozpoczęcia musi być przed godziną zakończenia.
          </Message>
          <div class="step-footer">
            <Button label="Wstecz" severity="secondary" @click="activateCallback('1')" />
            <Button label="Dalej" :disabled="!step2Valid"
                    @click="activateCallback('3'); loadAircraftStep()" />
          </div>
        </StepPanel>

        <StepPanel v-slot="{ activateCallback }" value="3">
          <div class="step-body">
            <label>Samolot (dostępne w wybranym oknie)</label>
            <Select v-model="form.aircraftId" :options="aircraftOptions"
                    optionLabel="label" optionValue="value" :loading="loadingAircraft"
                    placeholder="Wybierz samolot" class="w-full" />
            <Message v-if="!loadingAircraft && availableAircraft.length === 0" severity="info" :closable="false">
              Brak dostępnych samolotów w wybranym oknie.
            </Message>
          </div>
          <div class="step-footer">
            <Button label="Wstecz" severity="secondary" @click="activateCallback('2')" />
            <Button label="Dalej" :disabled="!form.aircraftId"
                    @click="activateCallback('4'); loadPilotsStep()" />
          </div>
        </StepPanel>

        <StepPanel v-slot="{ activateCallback }" value="4">
          <div class="step-body">
            <label>Pilot (dostępny + uprawnienie na typ samolotu)</label>
            <Select v-model="form.pilotId" :options="pilotOptions"
                    optionLabel="label" optionValue="value" :loading="loadingPilots"
                    placeholder="Wybierz pilota" class="w-full" />
            <Message v-if="!loadingPilots && availablePilots.length === 0" severity="info" :closable="false">
              Brak pilotów z uprawnieniem na ten typ samolotu w wybranym oknie.
            </Message>
          </div>
          <div class="step-footer">
            <Button label="Wstecz" severity="secondary" @click="activateCallback('3')" />
            <Button label="Dalej" :disabled="!form.pilotId" @click="activateCallback('5')" />
          </div>
        </StepPanel>

        <StepPanel v-slot="{ activateCallback }" value="5">
          <div class="step-body">
            <label>Lotnisko docelowe (opcjonalne)</label>
            <Select v-model="form.arrivalBaseId" :options="baseOptions"
                    optionLabel="label" optionValue="value" placeholder="(opcjonalne)"
                    class="w-full" showClear />
            <label>Opis (opcjonalny)</label>
            <InputText v-model="form.description" class="w-full" />
            <Message v-for="v in violations" :key="v.ruleId" severity="error" :closable="false">
              {{ v.ruleName }}: {{ v.message }}
            </Message>
            <Message v-if="submitError" severity="error" :closable="false">{{ submitError }}</Message>
          </div>
          <div class="step-footer">
            <Button label="Wstecz" severity="secondary" @click="activateCallback('4')" />
            <Button label="Utwórz lot" :loading="submitting" @click="submit" />
          </div>
        </StepPanel>
      </StepPanels>
    </Stepper>
  </Dialog>
</template>

<style scoped>
.step-body { display: flex; flex-direction: column; gap: 1rem; padding: 1rem 0; }
.step-body label { font-weight: 600; color: #3b82f6; font-size: 0.95rem; }
.step-body.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.step-footer { display: flex; gap: 0.5rem; justify-content: flex-end; margin-top: 1rem; }
.w-full { width: 100%; }
</style>
