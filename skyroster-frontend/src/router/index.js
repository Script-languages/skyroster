import { createRouter, createWebHistory } from 'vue-router'

const PilotsView = () => import('../views/PilotsView.vue')
const AircraftView = () => import('../views/AircraftView.vue')
const RulesView = () => import('../views/RulesView.vue')
const SchedulerView = () => import('../views/SchedulerView.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/harmonogram'
    },
    {
      path: '/harmonogram',
      name: 'harmonogram',
      component: SchedulerView
    },
    {
      path: '/piloci',
      name: 'piloci',
      component: PilotsView
    },
    {
      path: '/samoloty',
      name: 'samoloty',
      component: AircraftView
    },
    {
      path: '/zasady',
      name: 'zasady',
      component: RulesView
    }
  ]
})

export default router
