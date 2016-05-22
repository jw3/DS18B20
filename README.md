DS18B20 Interface
==========================
[![Build Status](https://travis-ci.org/jw3/DS18b20.svg?branch=master)](https://travis-ci.org/jw3/DS18B20)

Read from DS18B20 using the [`w1-gpio`](https://github.com/raspberrypi/firmware/tree/master/boot/overlays) module

Device is accessible at `/sys/bus/w1/devices/<id>/w1_slave`


```
Name:   w1-gpio
Info:   Configures the w1-gpio Onewire interface module.
        Use this overlay if you *don't* need a GPIO to drive an external pullup.
Load:   dtoverlay=w1-gpio,<param>=<val>
Params: gpiopin                 GPIO for I/O (default "4")

        pullup                  Non-zero, "on", or "y" to enable the parasitic
                                power (2-wire, power-on-data) feature


Name:   w1-gpio-pullup
Info:   Configures the w1-gpio Onewire interface module.
        Use this overlay if you *do* need a GPIO to drive an external pullup.
Load:   dtoverlay=w1-gpio-pullup,<param>=<val>
Params: gpiopin                 GPIO for I/O (default "4")

        pullup                  Non-zero, "on", or "y" to enable the parasitic
                                power (2-wire, power-on-data) feature

        extpullup               GPIO for external pullup (default "5")

```

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/jw3/DS18B20/issues).

## LICENSE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
