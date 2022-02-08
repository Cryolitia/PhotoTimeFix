namespace PhotoTimeFix.Util
{
    public class ProcessResult
    {
        public ProcessResult(string mFileName, string mDateTime, string mStatus, string mDetail)
        {
            FileName = mFileName;
            DateTime = mDateTime;
            Status = mStatus;
            Detail = mDetail;
        }

        public string FileName { get; set; }

        public string DateTime { get; set; }

        public string Status { get; set; }

        public string Detail { get; set; }
    }
}