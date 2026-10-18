# Gerçekçi Işıklandırma Modu

Minecraft Java Edition 1.20.1 + Forge 47.x için geliştirme sürümü.

## Özellikler
- Tek blokluk, kare, alüminyum reflektör ızgaralı geleneksel floresan.
- Tek blokluk, düz ve parlak yüzeyli kare modern LED panel.
- Işık Kontrol Kumandası ve Türkçe renk/parlaklık ayar menüsü.
- Geleneksel modelde üç renk; modern modelde RGB renkleri ve Kelvin sıcaklığı.
- 0–15 parlaklık seviyesi.
- Özel yaratıcı envanter sekmesi.

## Performans
- Kelvin renk geçişi artık her karede yüzlerce ayrı dikdörtgen çizmek yerine tek bir küçük doku ile çizilir.
- Kumanda aynı renk/sıcaklık/parlaklık değerini tekrar gönderirse blok ve ışık güncellemesi yapılmaz.
- Lambalar sürekli çalışan tick veya block entity kullanmaz.
- Bu değişiklikler modun kendi gereksiz çizim ve güncelleme işlerini azaltır; diğer modların, shader paketlerinin veya düşük Java bellek sınırının neden olduğu performans sorunlarını tek başına ortadan kaldırmayı garanti etmez.

## Işık sistemi notu
Standart Minecraft ışık motoru gerçek RGB ışık yayılımı sunmaz. Bu sürüm lambanın yüzey rengini ve beyaz sıcaklığını değiştirir, parlaklık ayarıyla blok ışık seviyesini 0–15 arasında yönetir. Gerçek renkli ortam ışığı bu sürümde henüz yer almıyor.

## Bulut derleme
GitHub Actions → Build Gercekci Isiklandirma Modu iş akışı Java 17 ile derleme yapar. Derleme başarısı oyun içinde test edilmişlik anlamına gelmez.
