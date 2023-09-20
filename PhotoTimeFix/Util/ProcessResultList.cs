using System.Collections.ObjectModel;

namespace PhotoTimeFix.Util
{
    public class ProcessResultList : ObservableCollection<ProcessResult>
    {
        public void Add(string mFileName, string mDateTime, string mStatus, string mDetail)
        {
            Add(new ProcessResult(mFileName, mDateTime, mStatus, mDetail));
        }
    }
}