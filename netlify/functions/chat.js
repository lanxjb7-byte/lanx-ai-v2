
exports.handler = async (event) => {
  const headers = {
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Headers": "Content-Type",
    "Access-Control-Allow-Methods": "POST, OPTIONS"
  };

  if (event.httpMethod === "OPTIONS") {
    return { statusCode: 204, headers, body: "" };
  }

  if (event.httpMethod !== "POST") {
    return {
      statusCode: 405,
      headers,
      body: JSON.stringify({ error: "Method not allowed" })
    };
  }

  const key = process.env.OPENROUTER_API_KEY;

  if (!key) {
    return {
      statusCode: 500,
      headers,
      body: JSON.stringify({ error: "OPENROUTER_API_KEY belum diatur" })
    };
  }

  try {
    const data = JSON.parse(event.body || "{}");

    if (!Array.isArray(data.messages) || !data.messages.length) {
      return {
        statusCode: 400,
        headers,
        body: JSON.stringify({ error: "Messages tidak valid" })
      };
    }

    if (event.body.length > 4500000) {
      return {
        statusCode: 413,
        headers,
        body: JSON.stringify({ error: "Request terlalu besar" })
      };
    }

    const messages = data.messages
      .filter(m =>
        m &&
        ["user", "assistant"].includes(m.role) &&
        m.content !== undefined
      )
      .slice(-20);

    const systemPrompt = data.hard_work
      ? "Kamu LANXX AI V2. Jawab secara teliti dan terstruktur. Untuk coding berikan kode lengkap beserta lokasi file. Periksa jawaban sebelum mengirim. Jangan mengklaim menjalankan kode jika belum. Berikan ringkasan, bukan proses berpikir internal."
      : "Kamu LANXX AI V2, asisten AI yang ramah, jelas, dan membantu.";

    const upstream = await fetch(
      "https://openrouter.ai/api/v1/chat/completions",
      {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${key}`,
          "Content-Type": "application/json",
          "HTTP-Referer": "https://lanx-ai-v2.netlify.app",
          "X-Title": "LANXX AI V2"
        },
        body: JSON.stringify({
          model: "openai/gpt-4o-mini",
          messages: [
            { role: "system", content: systemPrompt },
            ...messages
          ],
          max_tokens: data.hard_work ? 5000 : 2000,
          temperature: 0.6
        })
      }
    );

    return {
      statusCode: upstream.status,
      headers,
      body: await upstream.text()
    };

  } catch (error) {
    return {
      statusCode: 500,
      headers,
      body: JSON.stringify({ error: "Backend error" })
    };
  }
};
    
