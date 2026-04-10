import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { initialRules, RULE_TYPES, PERIODS } from '../data/mockData'

export const useRulesStore = defineStore('rules', () => {
  const rules = ref([...initialRules])
  const ruleTypeOptions = RULE_TYPES
  const periodOptions = PERIODS

  const rulesCount = computed(() => rules.value.length)

  function generateId() {
    const maxId = rules.value.reduce((max, rule) => {
      const num = parseInt(rule.id.replace('R', ''))
      return num > max ? num : max
    }, 0)
    return `R${String(maxId + 1).padStart(3, '0')}`
  }

  function addRule(ruleData) {
    const newRule = {
      ...ruleData,
      id: generateId()
    }
    rules.value.push(newRule)
    return newRule
  }

  function updateRule(id, ruleData) {
    const index = rules.value.findIndex(r => r.id === id)
    if (index !== -1) {
      rules.value[index] = { ...rules.value[index], ...ruleData }
      return rules.value[index]
    }
    return null
  }

  function deleteRule(id) {
    const index = rules.value.findIndex(r => r.id === id)
    if (index !== -1) {
      rules.value.splice(index, 1)
      return true
    }
    return false
  }

  function getRuleById(id) {
    return rules.value.find(r => r.id === id)
  }

  function getRuleTypeLabel(type) {
    const ruleType = ruleTypeOptions.find(t => t.value === type)
    return ruleType ? ruleType.label : type
  }

  function getPeriodLabel(period) {
    const periodOption = periodOptions.find(p => p.value === period)
    return periodOption ? periodOption.label : period
  }

  return {
    rules,
    rulesCount,
    ruleTypeOptions,
    periodOptions,
    addRule,
    updateRule,
    deleteRule,
    getRuleById,
    getRuleTypeLabel,
    getPeriodLabel
  }
})
