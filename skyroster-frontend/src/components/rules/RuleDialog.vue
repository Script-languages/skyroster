<script setup>
import { ref, computed } from 'vue'
import { useRulesStore } from '../../stores/rules'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Textarea from 'primevue/textarea'
import Select from 'primevue/select'
import Button from 'primevue/button'

const props = defineProps({
  visible: Boolean,
  rule: Object,
  isEditMode: Boolean
})

const emit = defineEmits(['update:visible', 'save'])

const rulesStore = useRulesStore()

const formData = ref(getEmptyForm())

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const dialogTitle = computed(() => props.isEditMode ? 'Edytuj zasadę' : 'Dodaj zasadę')

const valueLabel = computed(() => {
  switch (formData.value.typ) {
    case 'max_czas_pracy':
      return 'Maksymalna liczba godzin'
    case 'min_odpoczynek':
      return 'Minimalny czas odpoczynku (godziny)'
    case 'min_nalot':
      return 'Minimalna liczba godzin'
    default:
      return 'Wartość'
  }
})

function getEmptyForm() {
  return {
    nazwa: '',
    typ: null,
    wartosc: null,
    okres: null,
    opis: ''
  }
}

function resetForm() {
  if (props.rule && props.isEditMode) {
    formData.value = { ...props.rule }
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
      <div class="form-field full-width">
        <label for="nazwa">Nazwa zasady</label>
        <InputText id="nazwa" v-model="formData.nazwa" class="w-full" />
      </div>

      <div class="form-field">
        <label for="typ">Typ zasady</label>
        <Select
          id="typ"
          v-model="formData.typ"
          :options="rulesStore.ruleTypeOptions"
          optionLabel="label"
          optionValue="value"
          placeholder="Wybierz typ"
          class="w-full"
        />
      </div>

      <div class="form-field">
        <label for="okres">Okres</label>
        <Select
          id="okres"
          v-model="formData.okres"
          :options="rulesStore.periodOptions"
          optionLabel="label"
          optionValue="value"
          placeholder="Wybierz okres"
          class="w-full"
        />
      </div>

      <div class="form-field full-width">
        <label for="wartosc">{{ valueLabel }}</label>
        <InputNumber id="wartosc" v-model="formData.wartosc" class="w-full" :min="0" />
      </div>

      <div class="form-field full-width">
        <label for="opis">Opis</label>
        <Textarea id="opis" v-model="formData.opis" rows="3" class="w-full" />
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
