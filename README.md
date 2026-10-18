# Gerçekçi Işıklandırma Modu

Minecraft Java Edition 1.20.1 + Forge 47.2.0 için geliştirme sürümü.

## İçerik
- 1×1 blok boyutunda geleneksel alüminyum reflektör ızgaralı armatür.
- 1×1 blok boyutunda modern kare LED panel.
- Işık Kontrol Kumandası ve Türkçe renk kontrol menüsü.
- Geleneksel lambada üç renk, modern lambada 15 renk örneği ve 1000–10000 K arası Kelvin modu.
- Beş parlaklık modu: Çok düşük, Düşük, Orta, Yüksek, Çok yüksek.
- Bağımsız açma/kapama; kapalı lambanın yüzeyi nötr ve sönük görünür.
- Işık Kontrolü isimli özel yaratıcı sekmesi.

## Gerçek RGB ortam ışığı (isteğe bağlı uyumluluk)
Minecraft'ın temel ışık motoru komşu bloklara RGB renk dağıtmaz. Mod, Colored Lighting: Sodium/Embeddium Edition uyumluluğu için assets/gercekciisik/light/emitters.json dosyasını içerir. Gerçek renkli ortam ışığının ve lambanın çevresindeki renkli ışık taşmasının çalışması için istemcide Minecraft 1.20.1/Forge uyumlu Colorful Lighting (Sodium/Embeddium Edition) kurulmalıdır. Bu ek mod olmadan lamba yüzey rengi ve standart ışık seviyesi çalışır, fakat çevre aydınlatması beyaz kalır.

## Performans
- Renk indeksi BlockState içinde tutulur; renk seçimi render durumunu hemen günceller.
- Kelvin sıcaklığı blok varlığında saklanır; gereksiz sayıda BlockState kombinasyonu oluşturulmaz.
- Kelvin şeması tek doku üzerinden çizilir.
- Aynı ayarlar tekrar gönderildiğinde gereksiz blok/ışık güncellemesi atlanır.
- Lambalar sürekli çalışan tick sistemi kullanmaz.
- Bunlar modun kendi ek yükünü azaltmaya yönelik önlemlerdir; bütün bilgisayar performansını garanti etmez.

## Derleme ve test
GitHub Actions, Java 17 ile Forge modunu bulutta derler. Derlemenin başarılı olması oyun içinde test edildiği anlamına gelmez.
