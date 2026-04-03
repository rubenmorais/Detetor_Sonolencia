# 😴 Detetor de Sonolência

**Unidade Curricular:** Computação Móvel e Multisensorial  
**Autor:** Ruben Morais — Nº 28974

---

## Descrição

Aplicação Android desenvolvida em Kotlin com Jetpack Compose que monitoriza em tempo real sinais de sonolência no condutor, utilizando a câmara frontal do dispositivo e sensores de hardware.

---

## Funcionalidades

### 👁️ Deteção de Olhos
Através do **ML Kit Face Detection** da Google, a app analisa continuamente a probabilidade de abertura de cada olho. Se os olhos permanecerem fechados durante mais de **2 segundos**, é emitido um alerta de sonolência.

### 🤕 Deteção de Inclinação da Cabeça
O ângulo de inclinação da cabeça (eixo X) é monitorizado. Caso a cabeça caia para a frente mais de **20°**, sinal típico de microsono, o alerta é ativado de imediato, sem aguardar os 2 segundos.

### 🔔 Sistema de Alertas
Quando sonolência é detetada, a app ativa:
- **Vibração** em padrão repetido
- **Som de alarme** em volume máximo 


### 💡 Sensor de Luz
Monitoriza o nível de luminosidade ambiente. Caso a luz seja inferior a **50 lux**, é apresentado um aviso de ambiente escuro na interface.

### 🚗 Acelerómetro
Deteta se o dispositivo está em movimento com base na magnitude da aceleração, útil para contextualizar o uso da app durante a condução.

---

## Arquitetura

O projeto segue o padrão **MVVM** (Model-View-ViewModel):

```
MainActivity
│
├── ui/
│   ├── screens/
│   │   ├── EcraInicial       — ecrã de boas-vindas e arranque
│   │   └── EcraCamera        — ecrã principal de monitorização
│   ├── navigation/           — navegação entre ecrãs
│   └── theme/                — tema escuro da aplicação
│
├── viewmodel/
│   └── SonolenciaViewModel   — lógica de estado e deteção
│
├── camera/
│   ├── AnalisadorRosto       — análise de frames com ML Kit
│   └── GestorAlertas         — vibração e som de alarme
│
└── sensors/
    └── GestorSensores        — luz ambiente e acelerómetro
```
---

## Permissões Necessárias

- `CAMERA` — para aceder à câmara frontal e analisar o rosto
- `VIBRATE` — para ativar a vibração de alerta

---

## Como Executar

1. Clonar o repositório
2. Abrir no **Android Studio**
3. Compilar e instalar num dispositivo físico
4. Conceder a permissão de câmara quando solicitado
5. Pressionar **"Iniciar Detecção"**