#include <mmdeviceapi.h>
#include <endpointvolume.h>
#include "jni_VolumeControl.h"

/*
 * Class:     jni_VolumeControl
 * Method:    changeVolume
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_utils_jni_VolumeControl_changeVolume
  (JNIEnv *env, jclass cls, jfloat delta)
{
    HRESULT hResult = NULL;
    IMMDeviceEnumerator *deviceEnumerator = NULL;

    /* Initialize  the COM library on the current thread
     * and identifies the concurrency model as single-thread apartment (STA) */
    CoInitialize(NULL);

    hResult = CoCreateInstance(__uuidof(MMDeviceEnumerator), NULL, CLSCTX_INPROC_SERVER,
        __uuidof(IMMDeviceEnumerator), (LPVOID *)&deviceEnumerator);

    if (S_OK == hResult)
    {
        IMMDevice *defaultDevice = NULL;

        hResult = deviceEnumerator->GetDefaultAudioEndpoint(eRender, eConsole, &defaultDevice);
        deviceEnumerator->Release();
        deviceEnumerator = NULL;

        if (S_OK == hResult)
        {
            IAudioEndpointVolume *endpointVolume = NULL;

            hResult = defaultDevice->Activate(__uuidof(IAudioEndpointVolume),
                CLSCTX_INPROC_SERVER, NULL, (LPVOID *)&endpointVolume);
            defaultDevice->Release();
            defaultDevice = NULL;

            if (S_OK == hResult)
            {
                float currentVolume = 0;
                float newVolume = 0;

                endpointVolume->GetMasterVolumeLevel(&currentVolume);
                hResult = endpointVolume->GetMasterVolumeLevelScalar(&currentVolume);

                if (1.0 < (currentVolume + delta))
                {
                    newVolume = 1;
                }
                else if (0 > (currentVolume + delta))
                {
                    newVolume = 0;
                }
                else
                {
                    newVolume = currentVolume + delta;
                }

                /* In case that the speaker is muted */
                endpointVolume->SetMute(FALSE, NULL);
                /* Set the new speaker volume */
                hResult = endpointVolume->SetMasterVolumeLevelScalar(newVolume, NULL);
                endpointVolume->Release();
            }
        }
    }

    /* uninitialize com */
    CoUninitialize();
}