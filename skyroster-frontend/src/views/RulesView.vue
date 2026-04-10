<script setup>
import { ref } from 'vue'
import { useRulesStore } from '../stores/rules'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Toast from 'primevue/toast'
import ConfirmDialog from 'primevue/confirmdialog'
import Tag from 'primevue/tag'
import RuleDialog from '../components/rules/RuleDialog.vue'

const rulesStore = useRulesStore()
const toast = useToast()
const confirm = useConfirm()

const dialogVisible = ref(false)
const selectedRule = ref(null)
const isEditMode = ref(false)

function getRuleTypeSeverity(type) {
  switch (type) {
    case 'max_czas_pracy':
      return 'danger'
    case 'min_odpoczynek':
      return 'info'
    case 'min_nalot':
      return 'warn'
    default:
      return 'secondary'
  }
}

function formatValue(rule) {
  const periodLabel = rulesStore.getPeriodLabel(rule.okres).toLowerCase()
  return `${rule.wartosc} godz. / ${periodLabel}`
}

function openAddDialog() {
  selectedRule.value = null
  isEditMode.value = false
  dialogVisible.value = true
}

function openEditDialog(rule) {
  selectedRule.value = rule
  isEditMode.value = true
  dialogVisible.value = true
}

function handleSave(ruleData) {
  if (isEditMode.value && selectedRule.value) {
    rulesStore.updateRule(selectedRule.value.id, ruleData)
    toast.add({ severity: 'success', summary: 'Sukces', detail: 'Zasada została zaktualizowana', life: 3000 })
  } else {
    rulesStore.addRule(ruleData)
    toast.add({ severity: 'success', summary: 'Sukces', detail: 'Zasada została dodana', life: 3000 })
  }
}

function confirmDelete(rule) {
  confirm.require({
    message: `Czy na pewno chcesz usunąć zasadę "${rule.nazwa}"?`,
    header: 'Potwierdzenie usunięcia',
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: 'Tak, usuń',
    rejectLabel: 'Anuluj',
    accept: () => {
      rulesStore.deleteRule(rule.id)
      toast.add({ severity: 'success', summary: 'Sukces', detail: 'Zasada została usunięta', life: 3000 })
    }
  })
}
</script>

<template>
  <div class="rules-view">
    <Toast />
    <ConfirmDialog />

    <div class="page-header">
      <h1>Zasady operacyjne</h1>
      <Button label="Dodaj zasadę" icon="pi pi-plus" @click="openAddDialog" />
    </div>

    <DataTable
      :value="rulesStore.rules"
      stripedRows
      paginator
      :rows="10"
      :rowsPerPageOptions="[5, 10, 20]"
      tableStyle="min-width: 50rem"
    >
      <Column field="nazwa" header="Nazwa" sortable />
      <Column field="typ" header="Typ" sortable>
        <template #body="{ data }">
          <Tag :value="rulesStore.getRuleTypeLabel(data.typ)" :severity="getRuleTypeSeverity(data.typ)" />
        </template>
      </Column>
      <Column header="Wartość">
        <template #body="{ data }">
          {{ formatValue(data) }}
        </template>
      </Column>
      <Column field="opis" header="Opis" style="max-width: 300px">
        <template #body="{ data }">
          <span class="rule-description">{{ data.opis }}</span>
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

    <RuleDialog
      v-model:visible="dialogVisible"
      :rule="selectedRule"
      :isEditMode="isEditMode"
      @save="handleSave"
    />
  </div>
</template>

<style scoped>
.rules-view {
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

.rule-description {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}
</style>
