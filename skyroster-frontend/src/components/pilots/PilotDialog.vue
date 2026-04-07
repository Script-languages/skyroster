<script setup>
import { ref, computed } from 'vue'
import { usePilotsStore } from '../../stores/pilots'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import MultiSelect from 'primevue/multiselect'
import Button from 'primevue/button'

const props = defineProps({
  visible: Boolean,
  pilot: Object,
  isEditMode: Boolean
})

const emit = defineEmits(['update:visible', 'save'])

const pilotsStore = usePilotsStore()

const formData = ref(getEmptyForm())

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const dialogTitle = computed(() => props.isEditMode ? 'Edytuj pilota' : 'Dodaj pilota')

function getEmptyForm() {
  return {
    imie: '',
    nazwisko: '',
    licencja: '',
    kwalifikacje: [],
    typySamolotow: [],
    bazaMacierzysta: null
  }
}

function resetForm() {
  if (props.pilot && props.isEditMode) {
    formData.value = { ...props.pilot }
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
    :style="{ width: '500px' }"
    modal
    @show="resetForm"
  >
    <div class="form-grid">
      <div class="form-field">
        <label for="imie">Imię</label>
        <InputText id="imie" v-model="formData.imie" class="w-full" />
      </div>

      <div class="form-field">
        <label for="nazwisko">Nazwisko</label>
        <InputText id="nazwisko" v-model="formData.nazwisko" class="w-full" />
      </div>

      <div class="form-field">
        <label for="licencja">Numer licencji</label>
        <InputText id="licencja" v-model="formData.licencja" class="w-full" />
      </div>

      <div class="form-field">
        <label for="bazaMacierzysta">Baza macierzysta</label>
        <Select
          id="bazaMacierzysta"
          v-model="formData.bazaMacierzysta"
          :options="pilotsStore.baseOptions"
          optionLabel="label"
          optionValue="value"
          placeholder="Wybierz bazę"
          class="w-full"
        />
      </div>

      <div class="form-field full-width">
        <label for="kwalifikacje">Kwalifikacje</label>
        <MultiSelect
          id="kwalifikacje"
          v-model="formData.kwalifikacje"
          :options="pilotsStore.qualificationOptions"
          optionLabel="label"
          optionValue="value"
          placeholder="Wybierz kwalifikacje"
          class="w-full"
        />
      </div>

      <div class="form-field full-width">
        <label for="typySamolotow">Typy samolotów</label>
        <MultiSelect
          id="typySamolotow"
          v-model="formData.typySamolotow"
          :options="pilotsStore.aircraftTypeOptions"
          optionLabel="label"
          optionValue="value"
          placeholder="Wybierz typy samolotów"
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
</style>
