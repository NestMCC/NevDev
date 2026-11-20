# Nev Launcher — catatan proyek untuk Claude

Pemilik mengembangkan semuanya dari HP: **tidak ada build lokal**. Semua build lewat GitHub Actions.
Repo: NestMCC/NevDev, branch kerja: `tema-cyber-desain` (fork Zalith Launcher 2, basis PojavLauncher). Bahasa pemilik: Indonesia (gaya santai).

## Alur kerja
- Ubah kode -> commit -> `git push origin tema-cyber-desain` -> CI build.
- Pemilik bilang "Build gagal": `gh run list --branch tema-cyber-desain --limit 1`, lalu
  `gh run view <id> --log-failed | grep -E "e: file:|Unresolved|Location:"`, perbaiki, push.
- Kesalahan build yang sering: import hilang (`getValue/setValue`, `animateFloat`), opt-in `ExperimentalMaterial3ExpressiveApi`, parameter komposabel yang tidak ada (cek signature dulu).
- Akhir commit: trailer `Co-Authored-By: Claude ...` dan `Claude-Session: ...` sesuai sesi.
- Versi sekarang 4.0.0 (kode 400050) di `NevLauncher/gradle.properties`. Naikkan saat diminta.
- JANGAN hapus fitur lama saat memodifikasi; cek dulu dengan grep. Nama menu (Beranda, Server, File, dll) jangan diganti.

## Fitur yang sudah ada (jangan sampai hilang)
- Tema: Nev Lava, Cryogenic, Cyber (gaya desain + tata letak sendiri); "Hero" home; gaya kotak (boxy) global kecuali Cyber (`isBoxy()`, `BoxyButtons.kt`, `boxyBorder()`, import-swap tombol Material ke `ui.theme.*`).
- Tema desain **Liquid** (`ColorThemeType.LIQUID` + `DesignStyleType.LIQUID`, `ui/theme/LiquidStyle.kt`): palet kaca, latar cairan bergerak (`liquidBackdrop`), tepi kaca berputar (`liquidGlassRim` di BackgroundCard), efek gelombang saat tombol ditekan (`liquidPress` di `BoxyButtons.kt`), bentuk sangat bulat (bukan boxy). Berbagi animasi putar `LocalLiquidSpin`. Dipilih dari Pengaturan → Tampilan.
- Server Hub (cari, lokasi, online, game, favorit), unduh mod/modpack/shader dengan dependensi, kartu seri versi.
- Perekaman layar (+ noise cancelling), overlay FPS/koordinat/arah, Menu Game satu panel, musik (mini player), statistik waktu bermain.
- Kontrol: layout default NestMC, kontrol mengikuti tema (`controlFollowTheme`, `LocalControlAccent`), **Liquid** (`controlLiquid`, `LayerController/.../utils/LiquidEffect.kt`: kaca putih bening, gelombang cairan saat ditekan, garis tepi berputar).
- Backup dunia otomatis (`game/backup/WorldBackupManager.kt`; saat keluar + tiap 30 mnt; tab "Backup" di Kelola Versi; folder `versions/<instance>/backup-world/`).
- Analisis crash: `game/crash/CrashAnalyzer.kt` (aturan offline) + `AiCrashClient.kt` (opsional, API key milik pengguna) + `ui/components/CrashAnalysisDialog.kt`.
- Installer komponen di latar (`components/ComponentInstaller.kt`) dengan kartu progres di beranda hero; layar loading bermerek "NEV LAUNCHER" (`GameLoadingOverlay.kt`, `SplashScreen.kt`).
- `setting/SettingsGuard.kt`: cadangan pengaturan (MMKV) saat versi berubah + pemulihan bila kosong. Pengaturan baru harus lewat `AllSettings` dengan nilai bawaan agar update tidak mereset.
- Startup game: cache verifikasi file (`VerifiedFileCache`), cache pemindaian mod (`ModScanCache`), log waktu per tahap `LaunchTiming` di `GameLaunchFlow.kt`.
- Dialog pasang mod: kotak info dependensi otomatis + tombol hijau "+ INSTAL" (`_Download.Single.kt`).
- Donasi "Dukung Nev Launcher", akun Ely.by, folder data di beranda penyimpanan, kredit NestMC, teks "Nev Launcher" (pakai spasi).

## Pola kode
- Pengaturan: `boolSetting/intSetting/stringSetting/enumSetting` di `setting/AllSettings.kt` (MMKV lintas proses; proses `:game` menjalankan VMActivity).
- String: tambahkan ke `res/values` DAN `res/values-in`.
- PERINGATAN: jangan tambah flag JVM diagnostic/eksperimental (mis. `-XX:-BytecodeVerificationRemote`): di Java 25 itu membuat JVM gagal start (`IgnoreUnrecognizedVMOptions` tidak menolong). Uji dulu lewat log game.
- Belum dikerjakan/belum dikonfirmasi: mode hemat, dialog lemari pakaian bergaya kotak, halaman Favorit bergaya grid, fitur sosial, hapus APK lama otomatis di Actions.
