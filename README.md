# Gerçekçi Işıklandırma Modu

Minecraft Java Edition **1.20.1 + Forge 47.x** için erken geliştirme sürümü.

## Bu sürümde

- Kare, tek blok yer kaplayan geleneksel floresan: parabolik alüminyum ızgara görünümünü temsil eden yüzey ve üç renk seçeneği (soğuk mavi-beyaz, beyaz, mor-beyaz).
- Kare, tek blok yer kaplayan modern LED panel: kırmızı, turuncu, sarı, yeşil, turkuaz, açık mavi, mavi, mor, eflatun, pembe, mor-beyaz, sıcak ışık, beyaz ve soğuk beyaz renk seçenekleri.
- Işık kontrol kumandası: floresana kumandayla sağ tıklandığında Türkçe ayar ekranı açılır.
- Parlaklık 0–15 aralığında ayarlanır; 0 kapalı anlamına gelir.
- Lambalar ve kumanda özel yaratıcı sekmede yer alır; tarif eklenmediğinden Survival modunda normal olarak üretilemezler.
- GitHub Actions, Java 17 ve Gradle 8.1.1 kullanarak JAR derlemeyi dener.

## Önemli teknik sınır

Minecraft 1.20.1'in standart ışık motoru çevreye RGB renkli ışık yaymaz. Bu ilk sürüm panel yüzeyini seçilen renge boyar ve ışık seviyesini ayarlar; çevredeki bloklara gerçek renkli ışık yayılımı bu sürümde henüz uygulanmış değildir.

## Derleme

GitHub deposunda Actions → Build Gercekci Isiklandirma Modu → Run workflow yoluyla bulut derlemesini başlat. Başarıyla derlenirse gercekci-isiklandirma-jar adlı artifact oluşur. Derlemenin başarılı olması, oyunda tüm işlevlerin test edildiği anlamına gelmez; JAR'ı test dünyasında ayrıca dene.

Bu kaynak bir geliştirme sürümüdür; doğrulanmış Modrinth yayını olarak değerlendirilmemelidir.
