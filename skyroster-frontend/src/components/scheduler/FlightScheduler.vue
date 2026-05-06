<script setup>
import {computed, onBeforeUnmount, onMounted, ref, watch} from 'vue'
import {DayPilot, DayPilotScheduler} from '@daypilot/daypilot-lite-vue'
import {useFlightsStore} from '@/stores/flights.js'
import {usePilotsStore} from '@/stores/pilots.js'
import {useAircraftStore} from '@/stores/aircraft.js'

const emit = defineEmits(['eventClick', 'timeRangeSelected'])

const flightsStore = useFlightsStore()
usePilotsStore()
const aircraftStore = useAircraftStore()

const schedulerRef = ref(null)
const schedulerContainerRef = ref(null)

const initialStartDate = DayPilot.Date.today().firstDayOfMonth()

let resizeObserver = null
let resizeRaf = null

DayPilot.Locale.register(
  new DayPilot.Locale('pl-pl', {
    dayNames: ['Niedziela', 'Poniedziałek', 'Wtorek', 'Środa', 'Czwartek', 'Piątek', 'Sobota'],
    dayNamesShort: ['Nd', 'Pn', 'Wt', 'Śr', 'Cz', 'Pt', 'So'],
    monthNames: ['Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec', 'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'],
    monthNamesShort: ['Sty', 'Lut', 'Mar', 'Kwi', 'Maj', 'Cze', 'Lip', 'Sie', 'Wrz', 'Paź', 'Lis', 'Gru'],
    timePattern: 'HH:mm',
    datePattern: 'yyyy-MM-dd',
    dateTimePattern: 'yyyy-MM-dd HH:mm',
    timeFormat: 'Clock24Hours',
    weekStarts: 1
  })
)

const schedulerConfig = ref({
  locale: 'pl-pl',
  timeHeaders: [
    {groupBy: 'Month'},
    {groupBy: 'Day', format: 'd'}
  ],
  scale: 'Day',
  days: initialStartDate.daysInMonth(),
  startDate: initialStartDate,
  cellWidth: 40,
  eventHeight: 30,
  rowHeaderWidth: 150,
  treeEnabled: false,
  width: '100%',

  onEventClick: (args) => {
    const flight = flightsStore.getFlightById(args.e.id())
    if (flight) {
      emit('eventClick', flight)
    }
  },

  onTimeRangeSelected: (args) => {
    const aircraft = aircraftStore.getAircraftById(args.resource)
    emit('timeRangeSelected', {
      start: args.start,
      end: args.end,
      aircraftId: args.resource,
      aircraft: aircraft
    })
    schedulerRef.value?.control?.clearSelection()
  },

  allowEventOverlap: true,
  onBeforeEventRender: (args) => {
    const flight = flightsStore.getFlightById(args.data.id)
    if (flight) {
      const routeText = args.data.text || ''

      let arrival = routeText
      if (routeText.includes(' \u2192 ')) {
        arrival = routeText.split(' \u2192 ').pop()
      } else if (routeText.includes(' -> ')) {
        arrival = routeText.split(' -> ').pop()
      }

      args.data.html = `
        <div class="flight-event-wrapper">
          <span class="flight-full-text">${routeText}</span>
          <span class="flight-short-text">${arrival}</span>
        </div>
      `
      args.data.toolTip = routeText // pełny tekst po najechaniu myszką

      // Usuwamy rysowanie białego imienia i nazwiska
      args.data.areas = []
    }
  }
})

const resources = computed(() => flightsStore.getSchedulerResources())
const events = computed(() => flightsStore.getSchedulerEvents())

function getTimelineDays(startDate) {
  const containerWidth = schedulerContainerRef.value?.clientWidth ?? 0
  const rowHeaderWidth = schedulerConfig.value.rowHeaderWidth ?? 0
  const cellWidth = schedulerConfig.value.cellWidth ?? 40
  const minimumVisibleDays =
    containerWidth > rowHeaderWidth ? Math.ceil((containerWidth - rowHeaderWidth) / cellWidth) : 0

  return Math.max(startDate.daysInMonth(), minimumVisibleDays)
}

function updateTimeline(startDate) {
  if (!schedulerRef.value?.control) return

  const days = getTimelineDays(startDate)
  const control = schedulerRef.value.control
  if (control.days === days && control.startDate?.equals(startDate)) return

  control.update({
    startDate,
    days
  })
}

function updateTimelineForCurrentViewport() {
  if (!schedulerRef.value?.control) return

  const control = schedulerRef.value.control
  const days = getTimelineDays(control.startDate)
  if (control.days !== days) {
    control.update({days})
  }
}

function loadData() {
  if (schedulerRef.value?.control) {
    schedulerRef.value.control.update({
      resources: resources.value,
      events: events.value
    })
  }
}

function navigateToToday() {
  if (schedulerRef.value?.control) {
    schedulerRef.value.control.scrollTo(DayPilot.Date.today())
  }
}

function navigatePrevious() {
  if (schedulerRef.value?.control) {
    const currentStart = schedulerRef.value.control.startDate
    updateTimeline(currentStart.addMonths(-1))
  }
}

function navigateNext() {
  if (schedulerRef.value?.control) {
    const currentStart = schedulerRef.value.control.startDate
    updateTimeline(currentStart.addMonths(1))
  }
}

watch([resources, events], () => {
  loadData()
}, {deep: true})

onMounted(() => {
  if (typeof ResizeObserver !== 'undefined') {
    resizeObserver = new ResizeObserver(() => {
      if (resizeRaf) {
        cancelAnimationFrame(resizeRaf)
      }
      resizeRaf = requestAnimationFrame(() => {
        updateTimelineForCurrentViewport()
        resizeRaf = null
      })
    })

    if (schedulerContainerRef.value) {
      resizeObserver.observe(schedulerContainerRef.value)
    }
  }

  setTimeout(() => {
    loadData()
    updateTimeline(schedulerRef.value?.control?.startDate ?? schedulerConfig.value.startDate)
    navigateToToday()
  }, 100)
})

onBeforeUnmount(() => {
  if (resizeObserver) {
    resizeObserver.disconnect()
    resizeObserver = null
  }
  if (resizeRaf) {
    cancelAnimationFrame(resizeRaf)
    resizeRaf = null
  }
})

defineExpose({
  navigateToToday,
  navigatePrevious,
  navigateNext,
  loadData
})
</script>

<template>
  <div ref="schedulerContainerRef" class="flight-scheduler">
    <DayPilotScheduler ref="schedulerRef" :config="schedulerConfig"/>
  </div>
</template>

<style scoped>
.flight-scheduler {
  height: 100%;
  min-height: 500px;
  width: 100%;
}

.flight-scheduler :deep(.scheduler_default_main) {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.flight-scheduler :deep(.flight-event-wrapper) {
  container-type: inline-size;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.flight-scheduler :deep(.flight-full-text) {
  display: block;
  white-space: nowrap;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
}

.flight-scheduler :deep(.flight-short-text) {
  display: none;
  white-space: nowrap;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
}

@container (max-width: 110px) {
  .flight-scheduler :deep(.flight-full-text) {
    display: none;
  }

  .flight-scheduler :deep(.flight-short-text) {
    display: block;
  }
}
</style>
