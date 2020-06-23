package com.xinhhuynh.securitysp.exception

import java.lang.Exception

class SecretKeyNullPointException : Exception("Need to call func generateKey() first")