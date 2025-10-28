<template>
  <div class="allocation-chart">
    <h5 class="fw-bold mb-3">
      <i class="bi bi-pie-chart-fill text-primary me-2"></i>
      Asset Allocation
    </h5>

    <div v-if="!data || data.length === 0" class="text-center text-muted py-4">
      <i class="bi bi-inbox display-6 d-block mb-2"></i>
      <p>No allocation data available</p>
    </div>


    <v-chart
      v-else
      :option="chartOption"
      autoresize
      style="height: 350px; width: 100%;"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  }
})

const chartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {d}%'
  },
  legend: {
    bottom: 0,
    left: 'center'
  },
  series: [
    {
      name: 'Allocation',
      type: 'pie',
      radius: '70%',
      data: props.data.map(item => ({
        name: formatType(item.investmentType || item.assetType || item.type || 'Unknown'),
        value: Number(item.percentage || item.value || 0)
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        }
      },
      label: {
        formatter: '{b}\n{d}%',
        fontSize: 13
      }
    }
  ],
  color: [
    '#0d6efd',
    '#198754',
    '#ffc107',
    '#dc3545',
    '#20c997',
    '#6610f2',
    '#fd7e14'
  ]
}))

function formatType(type) {
  return type.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase())
}
</script>

<style scoped>
.allocation-chart {
  width: 100%;
}

h5 {
  font-weight: 600;
  color: #2c3e50;
}
</style>
