<template>
  <q-page class="q-pa-md">
    <div class="row justify-between items-center q-mb-md">
      <div class="text-h5 text-weight-bold text-secondary">Список сотрудников</div>
      <q-btn color="primary" icon="add" label="Добавить сотрудника" @click="showDialog = true" />
    </div>

    <q-table
      :rows="employees"
      :columns="columns"
      row-key="id"
      flat
      bordered
      :loading="loading"
    >
      <template v-slot:body-cell-actions="props">
        <q-td :props="props" class="q-gutter-xs">
          <q-btn size="sm" color="negative" icon="delete" round @click="deleteEmployee(props.row.id)" />
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="showDialog">
      <q-card style="width: 500px">
        <q-card-section><div class="text-h6">Новый сотрудник</div></q-card-section>
        <q-card-section class="q-gutter-md">
          <q-input v-model="newEmp.firstName" label="Имя" outlined dense />
          <q-input v-model="newEmp.lastName" label="Фамилия" outlined dense />
          <q-input v-model="newEmp.position" label="Должность" outlined dense />
          <q-input v-model="newEmp.department" label="Отдел" outlined dense />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Отмена" v-close-popup />
          <q-btn color="primary" label="Сохранить" @click="createEmployee" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from 'boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const employees = ref([])
const loading = ref(false)
const showDialog = ref(false)

const newEmp = ref({ firstName: '', lastName: '', position: '', department: '' })

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left', sortable: true },
  { name: 'lastName', label: 'Фамилия', field: 'lastName', align: 'left', sortable: true },
  { name: 'firstName', label: 'Имя', field: 'firstName', align: 'left' },
  { name: 'position', label: 'Должность', field: 'position', align: 'left' },
  { name: 'department', label: 'Отдел', field: 'department', align: 'left' },
  { name: 'hireDate', label: 'Дата найма', field: 'hireDate', align: 'left' },
  { name: 'actions', label: 'Действия', align: 'center' }
]

const fetchEmployees = async () => {
  loading.value = true
  try {
    const response = await api.get('/employees/')
    employees.value = response.data
  } catch {
    $q.notify({ color: 'negative', message: 'Ошибка загрузки данных' })
  } finally {
    loading.value = false
  }
}

const createEmployee = async () => {
  try {
    await api.post('/employees/', newEmp.value)
    showDialog.value = false
    newEmp.value = { firstName: '', lastName: '', position: '', department: '' }
    $q.notify({ color: 'positive', message: 'Сотрудник добавлен!' })
    fetchEmployees()
  } catch {
    $q.notify({ color: 'negative', message: 'Не удалось добавить сотрудника' })
  }
}

const deleteEmployee = async (id) => {
  $q.dialog({
    title: 'Подтверждение',
    message: 'Вы уверены, что хотите удалить сотрудника?',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await api.delete(`/employees/${id}`)
      $q.notify({ color: 'positive', message: 'Сотрудник удален' })
      fetchEmployees()
    } catch {
      $q.notify({ color: 'negative', message: 'Ошибка удаления' })
    }
  })
}

onMounted(() => {
  fetchEmployees()
})
</script>
