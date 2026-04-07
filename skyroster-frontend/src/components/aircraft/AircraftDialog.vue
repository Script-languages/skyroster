<script setup>
import { ref, computed } from 'vue'
import { useAircraftStore } from '../../stores/aircraft'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Button from 'primevue/button'

const props = defineProps({
  visible: Boolean,
  aircraft: Object,
  isEditMode: Boolean
})

const emit = defineEmits(['update:visible', 'save'])

const aircraftStore = useAircraftStore()

const formData = ref(getEmptyForm())

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const dialogTitle = computed(() => props.isEditMode ? 'Edytuj samolot' : 'Dodaj samolot')

function getEmptyForm() {
  return {
    rejestracja: '',
    typ: null,
    baza: null,
    dostepnosc: 'dostepny'
  }
}

function resetForm() {
  if (props.aircraft && props.isEditMode) {
    formData.value = { ...props.aircraft }
  } else {
    formData.value = getEmptyForm()
  }
}

function handleSave() {
  emit('save', { ...formData.value })
  dialogVisible.value = false
}

function handleCancel() {
  dialogVisible.value = false
}
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    :header="dialogTitle"
    :style="{ width: '450px' }"
    modal
    @show="resetForm"
  >
    <div class="form-grid">
      <div class="form-field">
        <label for="rejestracja">Rejestracja</label>
        <InputText id="rejestracja" v-model="formData.rejestracja" class="w-full" />
      </div>

      <div class="form-field">
        <label for="typ">Typ samolotu</label>
        <Select
          id="typ"
          v-model="formData.typ"
          :options="aircraftStore.aircraftTypeOptions"
          optionLabel="label"
          optionValue="value"
          placeholder="Wybierz typ"
          class="w-full"
        />
      </div>

      <div class="form-field">
        <label for="baza">Baza operacyjna</label>
        <Select
          id="baza"
          v-model="formData.baza"
          :options="aircraftStore.baseOptions"
          optionLabel="label"
          optionValue="value"
          placeholder="Wybierz bazę"
          class="w-full"
        />
      </div>

      <div class="form-field">
        <label for="dostepnosc">Dostępność</label>
        <Select
          id="dostepnosc"
          v-model="formData.dostepnosc"
          :options="aircraftStore.availabilityOptions"
          optionLabel="label"
          optionValue="value"
          placeholder="Wybierz status"
          class="w-full"
        />
      </div>
    </div>

    <template #footer>
      <Button label="Anuluj" severity="secondary" @click="handleCancel" />
      <Button label="Zapisz" @click="handleSave" />
    </template>
  </Dialog>
</template>

<style scoped>
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

.form-field label {
  font-weight: 600;
  color: #3b82f6;
  font-size: 0.95rem;
}

.w-full {
  width: 100%;
}
</style>
