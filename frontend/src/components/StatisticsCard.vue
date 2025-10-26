<template>
  <div class="col-md-4">
    <div class="card text-center shadow-sm">
      <div class="card-body">
        <h5 class="card-title text-muted">{{ title }}</h5>
        <p class="display-6 fw-bold" :class="valueClass">
          {{ value }}
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatisticsCard',
  
  props: {
    title: {
      type: String,
      required: true
    },
    value: {
      type: [String, Number],
      required: true
    },
    valueClass: {
      type: String,
      default: 'text-primary'
    }
  }
}
</script>

<style scoped>
.card {
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}
</style>
