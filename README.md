# Gerçekçi Işıklandırma Modu

Minecraft Java Edition 1.20.1 + Forge 47.2.0 için geliştirme sürümü.

## İçerik
- 1×1 blok boyutunda geleneksel alüminyum reflektör ızgaralı armatür.
- 1×1 blok boyutunda modern kare LED panel.
- Işık Kontrol Kumandası ve Türkçe renk kontrol menüsü.
- Geleneksel lambada üç sabit renk seçeneği.
- Modern lambada 15 renk örneği ve 1000 K–10000 K arası ayarlanabilir Kelvin modu.
- 0–15 parlaklık, bağımsız açma/kapama ve lamba blok durumunda saklanan ayarlar.
- Işık Kontrolü isimli özel yaratıcı sekmesi.

## Performans
- Kelvin şeması tek doku üzerinden çizilir.
- Aynı ayarlar tekrar gönderildiğinde gereksiz blok/ışık güncellemesi atlanır.
- Lambalar sürekli çalışan tick veya block entity kullanmaz.
- Opaque (tam opak) modelleri solid render katmanında bırakarak gereksiz cutout katmanından kaçınılır.
- Bunlar modun kendi ek yükünü azaltmaya yönelik önlemlerdir; tüm bilgisayar performansını garanti etmez.

## Gerçekçi ışık notu
Standart Minecraft blok ışığı RGB renkleri çevreye renkli biçimde yaymaz. Lamba yüzeyinin rengi seçilen RGB tonunu takip eder ve blok ışık seviyesi 0–15 arasında değişir. Gerçek renkli ortam ışığı için ayrı bir uyumlu aydınlatma/renderer çözümü gerekir.

## Derleme
GitHub Actions, Java 17 ile Forge modunu bulutta derler. Derlemenin başarılı olması, oyun içinde test edildiği anlamına gelmez.
