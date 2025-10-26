<template>
  <div class="col-md-4 col-lg-3">
    <div class="card text-center shadow-sm">
      <div class="card-body">
        <div class="stat-icon rounded-circle mx-auto mb-2" :class="iconClass">
          <i class="bi" :class="icon"></i>
        </div>
        <h3 class="fw-bold">{{ value }}</h3>
        <p class="text-muted mb-0">{{ label }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserStatCard',
  
  props: {
    icon: {
      type: String,
      required: true
    },
    iconClass: {
      type: String,
      required: true
    },
    value: {
      type: [Number, String],
      required: true
    },
    label: {
      type: String,
      required: true
    }
  }
}
</script>

<style scoped>
.stat-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.75rem;
}
</style>
