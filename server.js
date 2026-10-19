const express = require('express');
const cors = require('cors');
const { GoogleGenAI } = require('@google/genai');

const app = express();
app.use(cors());
app.use(express.json());

// API Keys und Tokens aus den Umgebungsvariablen laden
const GEMINI_API_KEY = process.env.GEMINI_API_KEY;
const APP_SECRET_TOKEN = process.env.APP_SECRET_TOKEN;

// Google GenAI Initialisierung
const ai = new GoogleGenAI({ apiKey: GEMINI_API_KEY });

// Middleware zur Prüfung des geheimen App-Tokens
function authenticateApp(req, res, next) {
    const clientToken = req.headers['x-app-token'];
    if (!APP_SECRET_TOKEN || clientToken !== APP_SECRET_TOKEN) {
        return res.status(403).json({ error: 'Unautorisierter Zugriff. Ungültiges App-Token.' });
    }
    next();
}

// Startseiten-Route (damit kein "Cannot GET /" erscheint)
app.get('/', (req, res) => {
    res.send('AI-Intellegens Server läuft ordnungsgemäß.');
});

// Prompt-Route für die Android-App (geschützt durch Token)
app.post('/generate', authenticateApp, async (req, res) => {
    try {
        const { prompt } = req.body;
        if (!prompt) {
            return res.status(400).json({ error: 'Prompt fehlt im Request Body.' });
        }

        const response = await ai.models.generateContent({
            model: 'gemini-3.8-flash',
            contents: prompt,
        });

        res.json({ text: response.text });
    } catch (error) {
        console.error('Fehler bei der Generierung:', error);
        res.status(500).json({ error: 'Fehler bei der Verarbeitung der KI-Anfrage.' });
    }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server läuft auf Port ${PORT}`);
});
