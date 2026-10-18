# Gerçekçi Işıklandırma Modu

Minecraft Java Edition 1.20.1 + Forge 47.2.0 için geliştirme sürümü.

## Işıklar
- Geleneksel alüminyum reflektör ızgaralı floresan ve modern kare LED panel.
- Kare ve daire spot ışıkları.
- Geleneksel simit armatür (beyaz, soğuk beyaz, kirli sarı) ve modern RGB/Kelvin simit armatür.
- Yatay ve dikey şerit LED'ler; aynı doğrultuda bitişik segmentler kumandayla bir grup olarak ayarlanır.
- Spot ve yatay LED şerit yerleştirmeye uygun LED yuvalı tavan paneli.
- Ahşap görünümlü, basamak yükseltilerinde renk ayarlı ışık çizgileri olan yeni LED merdiven bloğu.
- Işık Kontrol Kumandası: renk kareleri, Kelvin sıcaklığı, beş parlaklık seviyesi ve güç anahtarı.

## Parlaklık
Çok düşük / Düşük / Orta / Yüksek / Çok yüksek; standart Minecraft ışık seviyeleri 1 / 4 / 10 / 13 / 15. Shader ve renkli ışık motoru görünümü değiştirebildiğinden, nihai algılanan parlaklık oyun içinde doğrulanmalıdır.

## Gerçek RGB ortam ışığı
Standart Minecraft ışık motoru RGB rengini çevreye yaymaz. İstemcide **Colorful Lighting: Sodium/Embeddium Edition 2.7.0 veya uyumlu 1.20.1 sürümü** yüklüyse `assets/gercekciisik/light/emitters.json` içindeki NBT tabanlı renk kuralları lambaların seçilen renkte ışık yaymasına yardımcı olur. Renk değişimleri BlockEntity NBT'sine kaydedilir ve istemciye güncellenir.

## Performans
- Renk/Kelvin verileri BlockEntity'de saklanır, çok sayıda renk bazlı BlockState üretilmez.
- Renkli ışık kuralları emisyon seviyesini her zaman beş parlaklık modundan alır.
- Aynı ayarlar tekrar seçildiğinde gereksiz blok güncellemesi atlanır.
- Bu önlemler mod yükünü azaltmayı hedefler; bütün bilgisayar performansını garanti etmez.

## Test durumu
GitHub Actions Java 17 ile modun derlenmesini ve kaynak paketlerini doğrular. Derleme başarıyla sonuçlansa da oyun içi test ayrıca yapılmalıdır.
