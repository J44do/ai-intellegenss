const express = require('express');
const cors = require('cors');
const { GoogleGenAI } = require('@google/genai');

const app = express();
app.use(cors());
app.use(express.json());

const GEMINI_API_KEY = process.env.GEMINI_API_KEY;
const APP_SECRET_TOKEN = process.env.APP_SECRET_TOKEN;

const ai = new GoogleGenAI({ apiKey: GEMINI_API_KEY });

function authenticateApp(req, res, next) {
    const clientToken = req.headers['x-app-token'];
    if (!APP_SECRET_TOKEN || clientToken !== APP_SECRET_TOKEN) {
        return res.status(403).json({ error: 'Unautorisierter Zugriff. Ungültiges App-Token.' });
    }
    next();
}

app.get('/', (req, res) => {
    res.send('AI-Intellegens Server läuft ordnungsgemäß.');
});

// Route mit Unterstützung für Chat-Historie (Gedächtnis)
app.post('/generate', authenticateApp, async (req, res) => {
    try {
        const { prompt, history } = req.body;
        if (!prompt) {
            return res.status(400).json({ error: 'Prompt fehlt im Request Body.' });
        }

        // System-Prompt für Alexa-ähnlichen, freundlichen Sprachassistenten
        const systemInstruction = "Du bist ein intelligenter, eleganter Tablet-Sprachassistent namens Intellegens. Antworte präzise, klar, natürlich und prägnant.";

        let contents = [];
        if (history && Array.isArray(history)) {
            contents = history.map(item => ({
                role: item.role,
                parts: [{ text: item.text }]
            }));
        }
        contents.push({ role: 'user', parts: [{ text: prompt }] });

        const response = await ai.models.generateContent({
            model: 'gemini-3.8-flash',
            contents: contents,
            config: {
                systemInstruction: systemInstruction
            }
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
