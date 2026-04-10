<script setup>
import { ref } from 'vue'
import { useAircraftStore } from '../stores/aircraft'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Toast from 'primevue/toast'
import ConfirmDialog from 'primevue/confirmdialog'
import Tag from 'primevue/tag'
import AircraftDialog from '../components/aircraft/AircraftDialog.vue'

const aircraftStore = useAircraftStore()
const toast = useToast()
const confirm = useConfirm()

const dialogVisible = ref(false)
const selectedAircraft = ref(null)
const isEditMode = ref(false)

function getTypeLabel(typeCode) {
  const type = aircraftStore.aircraftTypeOptions.find(t => t.value === typeCode)
  return type ? type.label : typeCode
}

function getBaseLabel(baseCode) {
  const base = aircraftStore.baseOptions.find(b => b.value === baseCode)
  return base ? base.label : baseCode
}

function getAvailabilitySeverity(status) {
  const info = aircraftStore.getAvailabilityInfo(status)
  return info ? info.severity : 'secondary'
}

function getAvailabilityLabel(status) {
  const info = aircraftStore.getAvailabilityInfo(status)
  return info ? info.label : status
}

function openAddDialog() {
  selectedAircraft.value = null
  isEditMode.value = false
  dialogVisible.value = true
}

function openEditDialog(aircraft) {
  selectedAircraft.value = aircraft
  isEditMode.value = true
  dialogVisible.value = true
}

function handleSave(aircraftData) {
  if (isEditMode.value && selectedAircraft.value) {
    aircraftStore.updateAircraft(selectedAircraft.value.id, aircraftData)
    toast.add({ severity: 'success', summary: 'Sukces', detail: 'Samolot został zaktualizowany', life: 3000 })
  } else {
    aircraftStore.addAircraft(aircraftData)
    toast.add({ severity: 'success', summary: 'Sukces', detail: 'Samolot został dodany', life: 3000 })
  }
}

function confirmDelete(aircraft) {
  confirm.require({
    message: `Czy na pewno chcesz usunąć samolot ${aircraft.rejestracja}?`,
    header: 'Potwierdzenie usunięcia',
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: 'Tak, usuń',
    rejectLabel: 'Anuluj',
    accept: () => {
      aircraftStore.deleteAircraft(aircraft.id)
      toast.add({ severity: 'success', summary: 'Sukces', detail: 'Samolot został usunięty', life: 3000 })
    }
  })
}
</script>

<template>
  <div class="aircraft-view">
    <Toast />
    <ConfirmDialog />

    <div class="page-header">
      <h1>Samoloty</h1>
      <Button label="Dodaj samolot" icon="pi pi-plus" @click="openAddDialog" />
    </div>

    <DataTable
      :value="aircraftStore.aircraft"
      stripedRows
      paginator
      :rows="10"
      :rowsPerPageOptions="[5, 10, 20]"
      tableStyle="min-width: 50rem"
    >
      <Column field="rejestracja" header="Rejestracja" sortable />
      <Column field="typ" header="Typ" sortable>
        <template #body="{ data }">
          {{ getTypeLabel(data.typ) }}
        </template>
      </Column>
      <Column field="baza" header="Baza operacyjna" sortable>
        <template #body="{ data }">
          {{ getBaseLabel(data.baza) }}
        </template>
      </Column>
      <Column field="dostepnosc" header="Dostępność" sortable>
        <template #body="{ data }">
          <Tag :value="getAvailabilityLabel(data.dostepnosc)" :severity="getAvailabilitySeverity(data.dostepnosc)" />
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

    <AircraftDialog
      v-model:visible="dialogVisible"
      :aircraft="selectedAircraft"
      :isEditMode="isEditMode"
      @save="handleSave"
    />
  </div>
</template>

<style scoped>
.aircraft-view {
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

.action-buttons {
  display: flex;
  gap: 0.5rem;
}
</style>
