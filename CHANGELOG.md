# Changelog

## [0.1.0](https://github.com/joeyaurel/hytale-portals/compare/v0.0.1...v0.1.0) (2026-01-27)


### Features

* add portal network and database structures ([1167323](https://github.com/joeyaurel/hytale-portals/commit/116732369ee332fe401718b946140c1052c8bb09))
* initial commit ([e32583f](https://github.com/joeyaurel/hytale-portals/commit/e32583f380ab6b60580afee4a59e98e293865198))
* initialize portal system plugin ([2b1615d](https://github.com/joeyaurel/hytale-portals/commit/2b1615dd03725b83e2389fb6458e53c51ed9cbdd))
* **permissions:** simplify constants ([a61efcf](https://github.com/joeyaurel/hytale-portals/commit/a61efcf4da713085c1cbcc3720c39e2c0e9f35cd))
* **portals:** add bounds calculation and position check ([08e763a](https://github.com/joeyaurel/hytale-portals/commit/08e763a3db10ae29411119665dc3b01d94a0c0b6))
* **portals:** add event systems for block interaction ([645b6d4](https://github.com/joeyaurel/hytale-portals/commit/645b6d47e644f343e3a6b3e0ad126348c87c2671))
* **portals:** add GUI and logic for portal teleportation ([e8d09ec](https://github.com/joeyaurel/hytale-portals/commit/e8d09ecd85646ce60bddcf773037a7f9ede576c7))
* **portals:** add location-based portal detection and tests ([5234cae](https://github.com/joeyaurel/hytale-portals/commit/5234cae98f8bb96c95b48ef9c74c2f4f67dbb1d9))
* **portals:** add logging for portal actions and plugin setup ([262b2d9](https://github.com/joeyaurel/hytale-portals/commit/262b2d976cd345c1f37072f8065b1c0eb6b38850))
* **portals:** add portal cancelation command ([b51e353](https://github.com/joeyaurel/hytale-portals/commit/b51e35324529ddc7ab445d068186b443a90cbcc5))
* **portals:** add portal creation UI and command updates ([74ea667](https://github.com/joeyaurel/hytale-portals/commit/74ea667d9862dd721b0ad0992ce6d16b5e67375e))
* **portals:** add portal destination support ([bd68e15](https://github.com/joeyaurel/hytale-portals/commit/bd68e15f43d5788158b0bdc7d6b1e6515006e5ae))
* **portals:** add portal network editing functionality ([a96eed8](https://github.com/joeyaurel/hytale-portals/commit/a96eed8bf1f0082202a664f47ff2d12f1b21853c))
* **portals:** add portal network management commands ([f2ec9f6](https://github.com/joeyaurel/hytale-portals/commit/f2ec9f60afb380d7b4ba7a948a93e621c80afa86))
* **portals:** add PortalDoneCommand and implement portal DB actions ([2761b9d](https://github.com/joeyaurel/hytale-portals/commit/2761b9d26265f2deb09ccafc5046e43d2b98c800))
* **portals:** add Singleton annotation and inject dependencies ([05f0868](https://github.com/joeyaurel/hytale-portals/commit/05f08683e643e87de2c8a1436596b86974207208))
* **portals:** enhance portal bounds messaging with coordinates ([9afe38a](https://github.com/joeyaurel/hytale-portals/commit/9afe38a1720f6c46b028b377646e7e22e709afc3))
* **portals:** ensure main directory creation on startup ([74a3526](https://github.com/joeyaurel/hytale-portals/commit/74a35262d06f6fc97a642bd23b8a7637ab7f4ad7))
* **portals:** handle empty portal network list gracefully ([238dc8a](https://github.com/joeyaurel/hytale-portals/commit/238dc8ae6c3326f9a173e96696b072ad960eb1cf))
* **portals:** implement EntryTickingSystem for portal logic ([d4ce3f7](https://github.com/joeyaurel/hytale-portals/commit/d4ce3f742af49976b42ec10095c18cc1378e3eaf))
* **portals:** prevent duplicate portal network creation ([5560697](https://github.com/joeyaurel/hytale-portals/commit/5560697b139659f2a566aa31cb037837daa1be23))
* **portals:** refactor rotation handling for teleportation ([3621d3a](https://github.com/joeyaurel/hytale-portals/commit/3621d3ae85d657ca552b8c88a28bc9cebc1ed487))
* **portals:** remove portals when deleting their network ([6d5c6cd](https://github.com/joeyaurel/hytale-portals/commit/6d5c6cd8fcac0e9f2b0f2e69cde2c71c41b1db8c))
* **server:** update server version to 2026.01.24-6e2d4fc36 ([f4a9afc](https://github.com/joeyaurel/hytale-portals/commit/f4a9afc35e0c888e8d590b01a461d01e1ee6ae29))
* **server:** update server version to 2026.01.27-734d39026 ([08baaec](https://github.com/joeyaurel/hytale-portals/commit/08baaec18f42c2cfbc7f49bfae75a87ff7c610a0))
* **utils:** add rotation clipping utility with tests ([ce8a23d](https://github.com/joeyaurel/hytale-portals/commit/ce8a23dc9b23bc8619462fd55dd22370e9a6464a))


### Bug Fixes

* **portals:** adjust confirmation check order in removal command ([91409c7](https://github.com/joeyaurel/hytale-portals/commit/91409c7674007d25acf5273b8254b25fbc5b5952))
* **portals:** correct boundary conditions in portal validation ([3bcb548](https://github.com/joeyaurel/hytale-portals/commit/3bcb548c2e90a9c26020e017d9043d192cbcea15))
* **portals:** ensure configuration is saved on startup ([734f112](https://github.com/joeyaurel/hytale-portals/commit/734f112ecf1ea97c1882fbc509c15a03e263fc20))
* **portals:** fix incorrect variable usage in network creation ([ea3ee9d](https://github.com/joeyaurel/hytale-portals/commit/ea3ee9d86d4f352adfff979267b5d42c028a13bd))
* **portals:** sanitize network names by trimming quotes ([5d6bdc9](https://github.com/joeyaurel/hytale-portals/commit/5d6bdc95026c89d823304f00c4957a6724dfaa9a))
* **portals:** use eager singletons for bindings ([f6f3870](https://github.com/joeyaurel/hytale-portals/commit/f6f3870448f0a929338ec48292ac753154054c8c))
* **portals:** use start instead of setup method ([9e24b1b](https://github.com/joeyaurel/hytale-portals/commit/9e24b1b0284488c7cc65bcf8ccd0a5bb628cb7f9))
