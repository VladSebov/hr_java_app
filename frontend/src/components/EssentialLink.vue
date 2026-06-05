<template>
  <q-item clickable v-ripple :to="link">
    <q-item-section avatar v-if="icon">
      <q-icon :name="icon" />
    </q-item-section>
    <q-item-section>
      <q-item-label>{{ title }}</q-item-label>
      <q-item-label caption>{{ caption }}</q-item-label>
    </q-item-section>
  </q-item>
</template>

<script setup>
defineProps({
  title: String,
  caption: String,
  icon: String,
  link: String
})
</script>
