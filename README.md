# Gerçekçi Işıklandırma Modu

Minecraft Java Edition 1.20.1 + Forge 47.2.0 için geliştirme sürümü.

## İçerik
- 1×1 blok boyutunda geleneksel alüminyum reflektör ızgaralı armatür.
- 1×1 blok boyutunda modern kare LED panel.
- Işık Kontrol Kumandası ve Türkçe renk kontrol menüsü.
- Geleneksel lambada üç renk; modern lambada 15 renk örneği ve 1000–10000 K arası Kelvin modu.
- Beş parlaklık modu: Çok düşük, Düşük, Orta, Yüksek, Çok yüksek.
- Bağımsız açma/kapama; kapalı lambanın yüzeyi nötr ve sönük görünür.
- Işık Kontrolü isimli özel yaratıcı sekmesi.

## Gerçek renkli ortam ışığı
Minecraft/Forge'un standart ışık sistemi çevredeki bloklara RGB rengini yaymaz. Bunun için istemciye **Colorful Lighting: Sodium/Embeddium Edition** gerekir. Önerilen uyumlu sürüm: **Minecraft 1.20.1 Forge — Colorful Lighting 2.7.0**.

Modumuzun `assets/gercekciisik/light/emitters.json` dosyası Colorful Lighting'in belgelenmiş `auto` ışık kuralını kullanır: parlak difüzör yüzeyini örnekler ve lambanın renk sağlayıcısını uygular. Beş parlaklık modu, blok durumundaki gerçek ışık seviyesiyle yönetilir. Lamba kapalıyken ışık sıfırlanır.

**Shader uyumluluğu:** Colorful Lighting'in kendi belgelerine göre Oculus'ta Complementary ailesi için özel yama otomatik uygulanır. Complementary Reimagined r5.8.1 bu aileye dahildir; elle shaderpack düzenlemek normalde gerekmez. Shader yamalama/uyumluluk davranışı kullanılan Colorful Lighting sürümüne bağlıdır.

Colorful Lighting kurulmazsa lambanın kendi paneli seçilen renkte görünebilir, ancak çevreye yayılan ışık standart Minecraft davranışı nedeniyle beyaz kalır.

## Performans
- Renk indeksi BlockState içinde tutulur; renk seçimi render durumunu hemen günceller.
- Kelvin sıcaklığı blok varlığında saklanır.
- Kelvin şeması tek doku üzerinden çizilir.
- Aynı ayarlar tekrar gönderildiğinde gereksiz blok/ışık güncellemesi atlanır.
- Lambalar sürekli çalışan tick sistemi kullanmaz.
- Bunlar modun kendi ek yükünü azaltmaya yönelik önlemlerdir; bütün bilgisayar performansını garanti etmez.

## Derleme ve test
GitHub Actions, Java 17 ile Forge modunu bulutta derler. Derlemenin başarılı olması oyun içinde test edildiği anlamına gelmez.
