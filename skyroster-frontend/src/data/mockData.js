export const QUALIFICATIONS = [
  { label: 'PPL', value: 'PPL' },
  { label: 'CPL', value: 'CPL' },
  { label: 'ATPL', value: 'ATPL' },
  { label: 'IFR', value: 'IFR' },
  { label: 'Multi-Engine', value: 'ME' },
  { label: 'Night Rating', value: 'NR' }
]

export const AIRCRAFT_TYPES = [
  { label: 'Cessna 172', value: 'C172' },
  { label: 'Cessna 152', value: 'C152' },
  { label: 'Piper PA-28', value: 'PA28' },
  { label: 'Diamond DA40', value: 'DA40' },
  { label: 'Diamond DA42', value: 'DA42' },
  { label: 'Beechcraft King Air', value: 'BE90' }
]

export const BASES = [
  { label: 'Warszawa (EPWA)', value: 'EPWA' },
  { label: 'Kraków (EPKK)', value: 'EPKK' },
  { label: 'Gdańsk (EPGD)', value: 'EPGD' },
  { label: 'Wrocław (EPWR)', value: 'EPWR' },
  { label: 'Poznań (EPPO)', value: 'EPPO' }
]

export const AVAILABILITY_STATUSES = [
  { label: 'Dostępny', value: 'dostepny', severity: 'success' },
  { label: 'Niedostępny', value: 'niedostepny', severity: 'danger' },
  { label: 'W serwisie', value: 'serwis', severity: 'warn' }
]

export const RULE_TYPES = [
  { label: 'Maksymalny czas pracy', value: 'max_czas_pracy' },
  { label: 'Minimalny odpoczynek', value: 'min_odpoczynek' },
  { label: 'Minimalny nalot', value: 'min_nalot' }
]

export const PERIODS = [
  { label: 'Dzień', value: 'dzien' },
  { label: 'Tydzień', value: 'tydzien' },
  { label: 'Miesiąc', value: 'miesiac' }
]

export const initialPilots = [
  {
    id: 'P001',
    imie: 'Jan',
    nazwisko: 'Kowalski',
    licencja: 'PL-CPL-12345',
    kwalifikacje: ['CPL', 'IFR', 'ME'],
    typySamolotow: ['C172', 'PA28', 'DA42'],
    bazaMacierzysta: 'EPWA'
  },
  {
    id: 'P002',
    imie: 'Anna',
    nazwisko: 'Nowak',
    licencja: 'PL-ATPL-67890',
    kwalifikacje: ['ATPL', 'IFR', 'ME', 'NR'],
    typySamolotow: ['DA42', 'BE90'],
    bazaMacierzysta: 'EPKK'
  },
  {
    id: 'P003',
    imie: 'Piotr',
    nazwisko: 'Wiśniewski',
    licencja: 'PL-PPL-11111',
    kwalifikacje: ['PPL'],
    typySamolotow: ['C152', 'C172'],
    bazaMacierzysta: 'EPGD'
  },
  {
    id: 'P004',
    imie: 'Katarzyna',
    nazwisko: 'Wójcik',
    licencja: 'PL-CPL-22222',
    kwalifikacje: ['CPL', 'IFR'],
    typySamolotow: ['C172', 'DA40'],
    bazaMacierzysta: 'EPWA'
  }
]

export const initialAircraft = [
  {
    id: 'A001',
    rejestracja: 'SP-KWA',
    typ: 'C172',
    baza: 'EPWA',
    dostepnosc: 'dostepny'
  },
  {
    id: 'A002',
    rejestracja: 'SP-KKA',
    typ: 'DA42',
    baza: 'EPKK',
    dostepnosc: 'dostepny'
  },
  {
    id: 'A003',
    rejestracja: 'SP-GDA',
    typ: 'C152',
    baza: 'EPGD',
    dostepnosc: 'serwis'
  },
  {
    id: 'A004',
    rejestracja: 'SP-WRA',
    typ: 'PA28',
    baza: 'EPWR',
    dostepnosc: 'dostepny'
  },
  {
    id: 'A005',
    rejestracja: 'SP-POZ',
    typ: 'BE90',
    baza: 'EPPO',
    dostepnosc: 'niedostepny'
  }
]

export const initialRules = [
  {
    id: 'R001',
    nazwa: 'Maksymalny czas pracy miesięczny',
    typ: 'max_czas_pracy',
    wartosc: 100,
    okres: 'miesiac',
    opis: 'Pilot nie może przekroczyć 100 godzin pracy w miesiącu'
  },
  {
    id: 'R002',
    nazwa: 'Maksymalny czas pracy dzienny',
    typ: 'max_czas_pracy',
    wartosc: 10,
    okres: 'dzien',
    opis: 'Pilot nie może przekroczyć 10 godzin pracy dziennie'
  },
  {
    id: 'R003',
    nazwa: 'Minimalny odpoczynek między lotami',
    typ: 'min_odpoczynek',
    wartosc: 12,
    okres: 'dzien',
    opis: 'Wymagany minimalny odpoczynek 12 godzin między lotami'
  },
  {
    id: 'R004',
    nazwa: 'Minimalny nalot miesięczny',
    typ: 'min_nalot',
    wartosc: 10,
    okres: 'miesiac',
    opis: 'Pilot musi wykonać minimum 10 godzin nalotu miesięcznie'
  }
]

function getStartOfWeek() {
  const now = new Date()
  const dayOfWeek = now.getDay()
  const diff = now.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1)
  return new Date(now.setDate(diff))
}

function formatDateTime(date, hours, minutes = 0) {
  const d = new Date(date)
  d.setHours(hours, minutes, 0, 0)
  return d.toISOString().slice(0, 19)
}

export function generateInitialFlights() {
  const weekStart = getStartOfWeek()
  
  const flights = [
    {
      id: 'F001',
      aircraftId: 'A001',
      pilotId: 'P001',
      start: formatDateTime(weekStart, 8),
      end: formatDateTime(weekStart, 11),
      text: 'EPWA → EPKK',
      description: 'Lot rejsowy Warszawa - Kraków'
    },
    {
      id: 'F002',
      aircraftId: 'A002',
      pilotId: 'P002',
      start: formatDateTime(weekStart, 14),
      end: formatDateTime(weekStart, 17),
      text: 'EPKK → EPGD',
      description: 'Lot rejsowy Kraków - Gdańsk'
    },
    {
      id: 'F003',
      aircraftId: 'A001',
      pilotId: 'P004',
      start: formatDateTime(new Date(weekStart.getTime() + 86400000), 9),
      end: formatDateTime(new Date(weekStart.getTime() + 86400000), 12),
      text: 'EPWA → EPWR',
      description: 'Lot rejsowy Warszawa - Wrocław'
    },
    {
      id: 'F004',
      aircraftId: 'A004',
      pilotId: 'P001',
      start: formatDateTime(new Date(weekStart.getTime() + 86400000 * 2), 10),
      end: formatDateTime(new Date(weekStart.getTime() + 86400000 * 2), 14),
      text: 'EPWR → EPPO',
      description: 'Lot rejsowy Wrocław - Poznań'
    },
    {
      id: 'F005',
      aircraftId: 'A002',
      pilotId: 'P002',
      start: formatDateTime(new Date(weekStart.getTime() + 86400000 * 3), 8),
      end: formatDateTime(new Date(weekStart.getTime() + 86400000 * 3), 13),
      text: 'EPKK → EPWA',
      description: 'Lot rejsowy Kraków - Warszawa'
    }
  ]
  
  return flights
}

export const initialFlights = generateInitialFlights()
