using System;
using System.IO;

namespace PhotoTimeFix.Util
{
    public interface IFileProcessor
    {
        DateTime? GetFileDateTime(FileInfo fileInfo);
    }
}