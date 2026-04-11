<script setup>
import { ref } from 'vue'
import { usePilotsStore } from '../stores/pilots'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Toast from 'primevue/toast'
import ConfirmDialog from 'primevue/confirmdialog'
import Tag from 'primevue/tag'
import PilotDialog from '../components/pilots/PilotDialog.vue'

const pilotsStore = usePilotsStore()
const toast = useToast()
const confirm = useConfirm()

const dialogVisible = ref(false)
const selectedPilot = ref(null)
const isEditMode = ref(false)

function getBaseLabel(baseCode) {
  const base = pilotsStore.baseOptions.find(b => b.value === baseCode)
  return base ? base.label : baseCode
}

function openAddDialog() {
  selectedPilot.value = null
  isEditMode.value = false
  dialogVisible.value = true
}

function openEditDialog(pilot) {
  selectedPilot.value = pilot
  isEditMode.value = true
  dialogVisible.value = true
}

function handleSave(pilotData) {
  if (isEditMode.value && selectedPilot.value) {
    pilotsStore.updatePilot(selectedPilot.value.id, pilotData)
    toast.add({ severity: 'success', summary: 'Sukces', detail: 'Pilot został zaktualizowany', life: 3000 })
  } else {
    pilotsStore.addPilot(pilotData)
    toast.add({ severity: 'success', summary: 'Sukces', detail: 'Pilot został dodany', life: 3000 })
  }
}

function confirmDelete(pilot) {
  confirm.require({
    message: `Czy na pewno chcesz usunąć pilota ${pilot.imie} ${pilot.nazwisko}?`,
    header: 'Potwierdzenie usunięcia',
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: 'Tak, usuń',
    rejectLabel: 'Anuluj',
    accept: () => {
      pilotsStore.deletePilot(pilot.id)
      toast.add({ severity: 'success', summary: 'Sukces', detail: 'Pilot został usunięty', life: 3000 })
    }
  })
}
</script>

<template>
  <div class="pilots-view">
    <Toast />
    <ConfirmDialog />

    <div class="page-header">
      <h1>Piloci</h1>
      <Button label="Dodaj pilota" icon="pi pi-plus" @click="openAddDialog" />
    </div>

    <DataTable
      :value="pilotsStore.pilots"
      stripedRows
      paginator
      :rows="10"
      :rowsPerPageOptions="[5, 10, 20]"
      tableStyle="min-width: 50rem"
    >
      <Column field="imie" header="Imię" sortable />
      <Column field="nazwisko" header="Nazwisko" sortable />
      <Column field="licencja" header="Licencja" sortable />
      <Column field="bazaMacierzysta" header="Baza macierzysta" sortable>
        <template #body="{ data }">
          {{ getBaseLabel(data.bazaMacierzysta) }}
        </template>
      </Column>
      <Column field="kwalifikacje" header="Kwalifikacje">
        <template #body="{ data }">
          <div class="tags-container">
            <Tag v-for="qual in data.kwalifikacje" :key="qual" :value="qual" severity="info" />
          </div>
        </template>
      </Column>
      <Column field="typySamolotow" header="Typy samolotów">
        <template #body="{ data }">
          <div class="tags-container">
            <Tag v-for="type in data.typySamolotow" :key="type" :value="type" severity="secondary" />
          </div>
        </template>
      </Column>
      <Column header="Akcje" :exportable="false" style="min-width: 8rem">
        <template #body="{ data }">
          <div class="action-buttons">
            <Button icon="pi pi-pencil" severity="info" text rounded @click="openEditDialog(data)" />
            <Button icon="pi pi-trash" severity="danger" text rounded @click="confirmDelete(data)" />
          </div>
        </template>
      </Column>
    </DataTable>

    <PilotDialog
      v-model:visible="dialogVisible"
      :pilot="selectedPilot"
      :isEditMode="isEditMode"
      @save="handleSave"
    />
  </div>
</template>

<style scoped>
.pilots-view {
  padding: 1rem;
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

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}
</style>
