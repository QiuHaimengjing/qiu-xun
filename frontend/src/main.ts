import { createApp } from 'vue'
import pinia from './stores'
import 'vant/lib/index.css'
import 'vant/es/notify/style'
import 'vant/es/toast/style'
import '@/assets/css/main.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(pinia)
app.use(router)

app.mount('#app')
